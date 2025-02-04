package edu.uoc.practica.bd.uocdb.exercise2;

import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.FileUtilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class Exercise2InsertAndUpdateDataFromFile {

    private FileUtilities fileUtilities;

    public Exercise2InsertAndUpdateDataFromFile() {
        super();
        fileUtilities = new FileUtilities();
    }

    public static void main(String[] args) {
        Exercise2InsertAndUpdateDataFromFile app = new Exercise2InsertAndUpdateDataFromFile();
        app.run();
    }

    private void run() {
        List<List<String>> fileContents = null;

        try {
            fileContents = fileUtilities.readFileFromClasspath("exercise2.data");
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("ERROR: I/O error");
            e.printStackTrace();
        }

        if (fileContents == null) {
            return;
        }

        DBAccessor dbaccessor = new DBAccessor();
        dbaccessor.init();
        Connection conn = dbaccessor.getConnection();

        if (conn == null) {
            return;
        }

        // The aim of the following program fragment is to ensure the robustness of the program, because your database inserts/updates may have errors.  What is done is to confirm the operations at the end of the program if everything went well.
        
        try {
            conn.setAutoCommit(false); // Disable autoCommit to handle transactions manually
        } catch (SQLException e) {
            System.err.println("ERROR: Unable to disable autoCommit.");
            e.printStackTrace();
            return;
        }
        
        // TODO Prepare everything before updating or inserting
        String updateWinerySQL = "UPDATE WINERY SET winery_phone = ?, sales_representative = ? WHERE winery_id = ?";
        String insertWinerySQL = "INSERT INTO WINERY (winery_id, winery_name, town, established_year, winery_phone, sales_representative) VALUES (?, ?, ?, ?, ?, ?)";
        String selectZoneSQL = "SELECT zone_id FROM ZONE WHERE zone_id = ?";
        String insertZoneSQL = "INSERT INTO ZONE (zone_id, zone_name, capital_town, climate, region) VALUES (?, ?, ?, ?, ?)";
        String insertWineSQL = "INSERT INTO WINE (wine_name, vintage, alcohol_content, category, color, winery_id, zone_id, stock, price) VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?)";//As in the input variables(exercise2.data) there is no stock variable and it cannot be null, I have set it to 0 to guarantee a good functioning.
        
        try (
            PreparedStatement psUpdateWinery = conn.prepareStatement(updateWinerySQL);
            PreparedStatement psInsertWinery = conn.prepareStatement(insertWinerySQL);
            PreparedStatement psSelectZone = conn.prepareStatement(selectZoneSQL);
            PreparedStatement psInsertZone = conn.prepareStatement(insertZoneSQL);
            PreparedStatement psInsertWine = conn.prepareStatement(insertWineSQL)
        ) {
            // TODO Update or insert the wine, winery and zone from every row in file
            for (List<String> row : fileContents) {
                // Update winery
                setPSUpdateWinery(psUpdateWinery, row);
                int rowsUpdated = psUpdateWinery.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Winery updated: " + getValueIfNotNull(row.toArray(new String[0]), 7));
                } else {
                    // Insert winery if not updated
                    setPSInsertWinery(psInsertWinery, row);
                    psInsertWinery.executeUpdate();
                    System.out.println("New winery inserted: " + getValueIfNotNull(row.toArray(new String[0]), 7));
                }


                // Insert winery if not updated
                if (rowsUpdated == 0) {
                    setPSInsertWinery(psInsertWinery, row);
                    psInsertWinery.executeUpdate();
                }

                // Check if zone exists
                setPSSelectZone(psSelectZone, row);
                ResultSet rsZone = psSelectZone.executeQuery();
                if (!rsZone.next()) {
                    setPSInsertZone(psInsertZone, row);
                    psInsertZone.executeUpdate();
                    System.out.println("New zone inserted: " + getValueIfNotNull(row.toArray(new String[0]), 13));
                } else {
                    System.out.println("Zone already exists: " + getValueIfNotNull(row.toArray(new String[0]), 13));
                }


                // Insert wine
                setPSInsertWine(psInsertWine, row);
                psInsertWine.executeUpdate();
                System.out.println("New wine inserted: " + getValueIfNotNull(row.toArray(new String[0]), 0));

            }

            // TODO Validate transaction
            conn.commit();
            System.out.println("##########Transaction committed successfully.##########");
        } catch (SQLException e) {
            System.err.println("ERROR: Transaction failed, rolling back.");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        }
        // TODO Close resources and check exceptions
        finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setPSUpdateWinery(PreparedStatement updateStatement, List<String> row)
            throws SQLException {
        String[] rowArray = (String[]) row.toArray(new String[0]);

        setValueOrNull(updateStatement, 1, getValueIfNotNull(rowArray, 10)); // winery_phone
        setValueOrNull(updateStatement, 2, getValueIfNotNull(rowArray, 11)); // sales_representative
        setValueOrNull(updateStatement, 3,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 6))); // winery_id
    }

    private void setPSInsertWinery(PreparedStatement insertStatement, List<String> row)
            throws SQLException {
        String[] rowArray = (String[]) row.toArray(new String[0]);

        setValueOrNull(insertStatement, 1,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 6)));  // winery_id
        setValueOrNull(insertStatement, 2, getValueIfNotNull(rowArray, 7)); // winery_name
        setValueOrNull(insertStatement, 3, getValueIfNotNull(rowArray, 8)); // town
        setValueOrNull(insertStatement, 4,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 9)));  // established_year
        setValueOrNull(insertStatement, 5, getValueIfNotNull(rowArray, 10));  // winery_phone
        setValueOrNull(insertStatement, 6, getValueIfNotNull(rowArray, 11));  // sales_representative
    }

    private void setPSSelectZone(PreparedStatement updateStatement, List<String> row)
            throws SQLException {
        String[] rowArray = (String[]) row.toArray(new String[0]);

        setValueOrNull(updateStatement, 1,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 12))); // zone_id
    }

    private void setPSInsertZone(PreparedStatement insertStatement, List<String> row)
            throws SQLException {
        String[] rowArray = (String[]) row.toArray(new String[0]);

        setValueOrNull(insertStatement, 1,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 12)));  // zone_id
        setValueOrNull(insertStatement, 2, getValueIfNotNull(rowArray, 13)); // zone_name
        setValueOrNull(insertStatement, 3, getValueIfNotNull(rowArray, 14)); // capital_town
        setValueOrNull(insertStatement, 4, getValueIfNotNull(rowArray, 15));  // climate
        setValueOrNull(insertStatement, 5, getValueIfNotNull(rowArray, 16));  // region
    }

    private void setPSInsertWine(PreparedStatement insertStatement, List<String> row)
            throws SQLException {
        String[] rowArray = (String[]) row.toArray(new String[0]);

        setValueOrNull(insertStatement, 1, getValueIfNotNull(rowArray, 0)); // wine_name
        setValueOrNull(insertStatement, 2,
                getDoubleFromStringOrNull(getValueIfNotNull(rowArray, 1)));  // vintage
        setValueOrNull(insertStatement, 3,
                getDoubleFromStringOrNull(getValueIfNotNull(rowArray, 2))); // alcohol_content
        setValueOrNull(insertStatement, 4, getValueIfNotNull(rowArray, 3));  // category
        setValueOrNull(insertStatement, 5, getValueIfNotNull(rowArray, 4));  // color
        setValueOrNull(insertStatement, 6,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 6)));  // winery_id
        setValueOrNull(insertStatement, 7,
                getIntegerFromStringOrNull(getValueIfNotNull(rowArray, 12)));  // zone_id
        setValueOrNull(insertStatement, 8,
                getDoubleFromStringOrNull(getValueIfNotNull(rowArray, 5)));  // price
    }

    private Integer getIntegerFromStringOrNull(String integer) {
        return (integer != null) ? Integer.valueOf(integer) : null;
    }

    private Double getDoubleFromStringOrNull(String doubl) {
        return (doubl != null) ? Double.valueOf(doubl) : null;
    }

    private String getValueIfNotNull(String[] rowArray, int index) {
        return (index < rowArray.length && rowArray[index].length() > 0) ? rowArray[index] : null;
    }

    private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex,
                                Integer value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, Types.INTEGER);
        } else {
            preparedStatement.setInt(parameterIndex, value);
        }
    }

    private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex,
                                Double value) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, Types.DOUBLE);
        } else {
            preparedStatement.setDouble(parameterIndex, Double.valueOf(value.doubleValue()));
        }
    }

    private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex, String value)
            throws SQLException {
        if (value == null || value.length() == 0) {
            preparedStatement.setNull(parameterIndex, Types.VARCHAR);
        } else {
            preparedStatement.setString(parameterIndex, value);
        }
    }

}

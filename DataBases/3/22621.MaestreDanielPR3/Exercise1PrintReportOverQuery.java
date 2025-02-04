package edu.uoc.practica.bd.uocdb.exercise1;

import edu.uoc.practica.bd.util.Column;
import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.Report;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exercise1PrintReportOverQuery {

    public static void main(String[] args) {
        Exercise1PrintReportOverQuery app = new Exercise1PrintReportOverQuery();
        app.run();
    }

    private void run() {
        DBAccessor dbaccessor = new DBAccessor();
        dbaccessor.init();
        Connection conn = dbaccessor.getConnection();

        if (conn != null) {
            Statement stmt = null;
            ResultSet resultSet = null;

            try {
                List<Column> columns = Arrays.asList(
                        new Column("Zone", 12, "zone_name"),
                        new Column("Capital", 12, "capital_town"),
                        new Column("Climate", 15, "climate"),
                        new Column("Region", 20, "region"),
                        new Column("Last selling", 12, "last_selling"),
                        new Column("Total", 5, "total_quantity")
                );

                Report report = new Report();
                report.setColumns(columns);
                List<Object> list = new ArrayList<>();

                // TODO Execute SQL sentence
                String sql = "SELECT zone_name, capital_town, climate, region, last_selling, total_quantity FROM best_selling_zones";
                stmt = conn.createStatement();
                resultSet = stmt.executeQuery(sql);

                // TODO Loop over results and get the main values
                while (resultSet.next()) {
                    Exercise1Row row = new Exercise1Row(
                            resultSet.getString("zone_name"),
                            resultSet.getString("capital_town"),
                            resultSet.getString("climate"),
                            resultSet.getString("region"),
                            resultSet.getString("last_selling"),
                            resultSet.getLong("total_quantity")
                    );
                    list.add(row);
                }

                // TODO End loop
                report.printReport(list);

            } catch (SQLException e) {
                System.err.println("ERROR: List not available");
                e.printStackTrace();
            } finally {
                // TODO Close All resources
                try {
                    if (resultSet != null) resultSet.close();
                    if (stmt != null) stmt.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package uoc.ds.pr.util;

import uoc.ds.pr.LibraryPR3;

public class LevelHelper {

        public static LibraryPR3.Level getLevel(int points) {
            LibraryPR3.Level level = LibraryPR3.Level.TROLL;
            if (points<-10) {
                level = LibraryPR3.Level.TROLL;
            }
            else if (points>=-10 && points<0)  {
                level =  LibraryPR3.Level.MUGGLE;
            }
            else if (points>=0 && points<10 ) {
                level =  LibraryPR3.Level.OOMPA_LOOMPA;
            }
            else if (points>=10 && points < 20 ) {
                level = LibraryPR3.Level.HOBBIT;
            }
            else if (points>=20 && points < 30 ) {
                level = LibraryPR3.Level.FREMEN;
            }
            else if (points>=30 && points < 40 ) {
                level = LibraryPR3.Level.WINDRUNNER;
            }
            else if (points>=40) {
                level = LibraryPR3.Level.ASLAN;
            }
            return level;
        }

}

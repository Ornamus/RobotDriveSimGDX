package ryan.game.competition;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Schedule {

    public List<Match> matches = new ArrayList<>();

    public void generate(List<Team> teams, int rounds) {
        try {
            //Utils.log("Executing process");
            final Process p = Runtime.getRuntime().exec("\"core/assets/matchmaker\" -t " + teams.size() + " -r " + rounds + " > matches_temp.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String data = "";
            String out;
            while ((out = reader.readLine()) != null) {
                data += out + "\n";
                //Utils.log(out);
            }
            p.waitFor();
            data = data.substring(data.indexOf("--------------"), data.indexOf("Schedule Statistics"));
            String[] lines = data.split("\n");
            for (String s : lines) {
                if (s.contains(":")) {
                    String info = s;
                    while (info.contains("  ")) info = info.replace("  ", " ");
                    String[] parts = info.split(" ");
                    //Utils.log("s: " + s + ", info: " + info);
                    int matchNum = Integer.parseInt(parts[1].replace(":", ""));
                    //Utils.log("Match " + matchNum + ": " + info);

                    Team[] blue = new Team[3];
                    Team[] red = new Team[3];
                    List<Integer> blueSurrs = new ArrayList<>();
                    List<Integer> redSurrs = new ArrayList<>();
                    for (int i=2; i<5; i++) {
                        String part = parts[i];
                        if (part.contains("*")) {
                            blueSurrs.add(i-2);
                            part = part.replace("*", "");
                        }
                        blue[i-2] = teams.get(Integer.parseInt(part) - 1);
                    }

                    for (int i=5; i<8; i++) {
                        String part = parts[i];
                        if (part.contains("*")) {
                            redSurrs.add(i-5);
                            part = part.replace("*", "");
                        }
                        red[i-5] = teams.get(Integer.parseInt(part) - 1);
                    }
                    Match m = new Match(matchNum, blue, red);
                    m.setBlueSurrogates(blueSurrs);
                    m.setRedSurrogates(redSurrs);
                    matches.add(m);
                    //Utils.log(m.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Match {

        public final int number;
        public final Team[] blue, red;
        public List<Integer> blueSurrogates = new ArrayList<>();
        public List<Integer> redSurrogates = new ArrayList<>();

        public Match(int number, Team[] b, Team[] r) {
            this.number = number;
            blue = b;
            red = r;
        }

        public void setBlueSurrogates(List<Integer> indexes) {
            blueSurrogates = indexes;
        }

        public void setRedSurrogates(List<Integer> indexes) {
            redSurrogates = indexes;
        }

        @Override
        public String toString() {
            String blueString = "", redString = "";
            for (int i=0; i<blue.length; i++) {
                Team t = blue[i];
                boolean surrogate = blueSurrogates.contains(i);
                blueString += t.number + (surrogate ? "*" : "") + (i+1 != blue.length ? ", " : "");
            }

            for (int i=0; i<red.length; i++) {
                Team t = red[i];
                boolean surrogate = redSurrogates.contains(i);
                redString += t.number + (surrogate ? "*" : "") + (i+1 != red.length ? ", " : "");
            }

            return "Match " + number + ": " + blueString + "  vs  " + redString;
        }
    }
}

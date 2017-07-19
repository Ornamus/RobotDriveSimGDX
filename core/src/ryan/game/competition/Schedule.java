package ryan.game.competition;

import ryan.game.Main;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private List<Team> teams = new ArrayList<>();
    private int current = 0;
    public List<Match> matches = new ArrayList<>();
    private Rankings r;

    public Schedule(Rankings r) {
        this.r = r;
        r.s = this;
    }

    public void generate(List<Team> teams, int rounds) {
        this.teams = teams;
        if (Main.makeSchedule) {
            if (teams.size() >= 6) {
                try {
                    final Process p = Runtime.getRuntime().exec("\"core/assets/matchmaker\" -t " + teams.size() + " -r " + rounds + " -o");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String data = "";
                    String out;
                    while ((out = reader.readLine()) != null) {
                        data += out + "\n";
                    }
                    p.waitFor();
                    data = data.substring(data.indexOf("--------------"), data.indexOf("Schedule Statistics"));
                    String[] lines = data.split("\n");
                    for (String s : lines) {
                        if (s.contains(":")) {
                            String info = s;
                            while (info.contains("  ")) info = info.replace("  ", " ");
                            String[] parts = info.split(" ");

                            int matchNum = Integer.parseInt(parts[1].replace(":", ""));

                            Team[] blue = new Team[3];
                            Team[] red = new Team[3];
                            List<Integer> blueSurrs = new ArrayList<>();
                            List<Integer> redSurrs = new ArrayList<>();
                            for (int i = 2; i < 5; i++) {
                                String part = parts[i];
                                if (part.contains("*")) {
                                    blueSurrs.add(i - 2);
                                    part = part.replace("*", "");
                                }
                                blue[i - 2] = teams.get(Integer.parseInt(part) - 1);
                            }

                            for (int i = 5; i < 8; i++) {
                                String part = parts[i];
                                if (part.contains("*")) {
                                    redSurrs.add(i - 5);
                                    part = part.replace("*", "");
                                }
                                red[i - 5] = teams.get(Integer.parseInt(part) - 1);
                            }
                            int[] blueNums = new int[3];
                            for (int i = 0; i < blue.length; i++) blueNums[i] = blue[i].number;

                            int[] redNums = new int[3];
                            for (int i = 0; i < red.length; i++) redNums[i] = red[i].number;

                            Match m = new Match(matchNum, blueNums, redNums);
                            m.setBlueSurrogates(blueSurrs);
                            m.setRedSurrogates(redSurrs);
                            matches.add(m);
                            //Utils.log(m.toString());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //TODO: error: need 6 teams minimum
            }
        } else {
            Match m = new Match(1, new int[]{1902, 254, 987}, new int[]{118, 811, 254});
            m.qualifier = false;
            matches.add(m);
        }
    }

    public int getCurrentMatchIndex() {
        return current;
    }

    public Match getCurrentMatch() {
        return getMatch(current);
    }

    public Match getMatch(int index) {
        if (matches.size() > index)
            return matches.get(index);
        else {
            return null;
        }
    }

    public List<Match> getQualifiers() {
        List<Match> m = new ArrayList<>();
        for (Match match : matches) {
            if (match.qualifier) m.add(match);
        }
        return m;
    }

    public void completeCurrentMatch(int blueScore, int redScore, Object blueBreakdown, Object redBreakdown) {
        completeMatch(current, blueScore, redScore, blueBreakdown, redBreakdown);
    }

    public void completeMatch(int index, int blueScore, int redScore, Object blueBreakdown, Object redBreakdown) {
        if (Main.makeSchedule) {
            Match m = getMatch(index);

            m.blue.score = blueScore;
            m.blue.winner = blueScore > redScore;
            m.blue.breakdown = blueBreakdown;

            m.red.score = redScore;
            m.red.winner = redScore > blueScore;
            m.red.breakdown = redBreakdown;

            m.complete = true;

            m.save();

            current++;

            r.calculate();
        }
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }
}

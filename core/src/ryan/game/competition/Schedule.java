package ryan.game.competition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.games.AllianceSelection;
import ryan.game.games.steamworks.AllianceScoreData;
import ryan.game.team254.utils.Path;

import java.io.*;
import java.util.*;

public class Schedule {

    private List<Team> teams = new ArrayList<>();
    private int current = 0;
    public List<Match> matches = new ArrayList<>();
    private Rankings r;

    public boolean elims = false;
    public int[][] alliances = new int[8][3];
    int[][] remainingAlliances = new int[8][3];

    private HashMap<Integer, List<Double>> seeds = new HashMap<>();

    public Schedule(Rankings r) {
        this.r = r;
        r.s = this;
        File f = new File(Main.eventKey  + "/teams.json");
        if (f.exists()) {
            Team[] teamArray = new Team[0];
            teamArray = Utils.fromJSON(f, teamArray.getClass());
            for (Team t : teamArray) {
                teams.add(t);
            }
            Utils.log("Loaded " + teams.size() + " teams");
        } else {
            List<Integer> taken = new ArrayList<>();
            for (int i = 0; i < Main.randomTeams; i++) {
                int num;
                while (taken.contains((num = Utils.randomInt(1, 6499)))) {
                }
                taken.add(num);
                teams.add(new Team(num, "null"));
            }
            Gson g = new GsonBuilder().setPrettyPrinting().create();
            Utils.writeFile(Main.eventKey + "/teams.json", g.toJson(teams));
        }
    }

    public void generate(int rounds) {
        if (Main.makeSchedule) {
            File f = new File(Main.eventKey + "/matches");
            File[] matchFiles;
            if (f.exists() && (matchFiles = f.listFiles()).length > 0) {
                for (File matchFile : matchFiles) {
                    Gson g = new Gson();
                    Match m = Utils.fromJSON(matchFile, Match.class);

                    //TODO: This is Steamworks-specific unfortunately
                    if (m.complete && m.blue.breakdown != null && m.red.breakdown != null) {
                        m.blue.breakdown = g.fromJson(m.blue.breakdown.toString(), AllianceScoreData.class);
                        m.red.breakdown = g.fromJson(m.red.breakdown.toString(), AllianceScoreData.class);
                    }

                    matches.add(m);
                }
                matches.sort((o1, o2) -> {
                    int result = o1.number - o2.number;
                    return result;
                });

                boolean found = false;
                int index = 0;
                for (Match m : getQualifiers()) {
                    if (!m.complete) {
                        current = index;
                        found = true;
                        break;
                    }
                    index++;
                }
                r.calculate();
                if (found) {
                    Main.getInstance().gameField.updateMatchInfo();
                    Utils.log("Left off at qualifier index " + index + ", restarting there.");
                } else if (!found) { //All qualifiers completed
                    f = new File(Main.eventKey + "/alliance_selection.json");
                    if (f.exists()) { //We have alliance selection data
                        Utils.log("Found alliance selection data");
                        elims = true;
                        alliances = Utils.fromJSON(f, alliances.getClass());
                        seeds = Utils.fromJSON(Main.eventKey + "/seeds.json", seeds.getClass());
                        /*for (Integer[] i : seeds.values()) {
                            Utils.log(i.toString());
                        }*/
                        remainingAlliances = Utils.fromJSON(Main.eventKey + "/remainingAlliances.json", remainingAlliances.getClass());
                        String[] levelsToDelete = {"qm"};
                        if (remainingAlliances.length == 2) levelsToDelete = new String[]{"qm", "qf", "sf"};
                        if (remainingAlliances.length == 4) levelsToDelete = new String[]{"qm", "qf"};
                        for (Match m : new ArrayList<>(matches)) {
                            for (String s : levelsToDelete) {
                                if (m.getLevel().equalsIgnoreCase(s)) {
                                    matches.remove(m);
                                    break;
                                }
                            }
                        }
                        matches.sort((o1, o2) -> {
                            int result = o1.number - o2.number;
                            return result;
                        });
                        index = 0;
                        for (Match m : matches) {
                            if (!m.complete) {
                                current = index;
                                break;
                            }
                            index++;
                        }
                        Utils.log(index + " of " + matches.size());
                        Main.getInstance().gameField.updateMatchInfo();
                    } else { //We need to start/restart alliance selection
                        Utils.log("Did not find alliance selection data, starting alliance selections.");
                        Main.allianceSelection = new AllianceSelection();
                        Main.getInstance().addDrawable(Main.allianceSelection);
                    }
                }
            } else {
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
                                m.save();
                                //Utils.log(m.toString());
                            }
                        }
                        Main.getInstance().gameField.updateMatchInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.log("[ERROR] A schedule requires at least 6 teams!");
                    //error: need 6 teams minimum
                }
            }
        } else {
            Match m = new Match(1, new int[]{1902, 254, 987}, new int[]{118, 811, 254});
            m.qualifier = false;
            matches.add(m);
        }
    }

    public void startElims(int[][] a) {
        elims = true;
        alliances = a;
        int seed = 1;
        for (int[] all : alliances) {
            List<Double> proper = new ArrayList<>();
            for (int i=0; i<all.length; i++) {
                proper.add(all[i] * 1.0);
            }
            seeds.put(seed, proper);
            seed++;
        }
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        Utils.writeFile(Main.eventKey + "/seeds.json", g.toJson(seeds));

        remainingAlliances = alliances;
        Utils.writeFile(Main.eventKey + "/remainingAlliances.json", g.toJson(remainingAlliances));
        generateNextElimMatches();
    }

    public void elimsUpdate() {
        HashMap<int[], Boolean> wonSet = new HashMap<>();
        for (int[] a : remainingAlliances) {
            wonSet.put(a, false);
        }

        for (int[] a : remainingAlliances) {
            int wins = 0;
            for (Match m : matches) {
                if (m.isInMatch(a[0])) {
                    if (arraysEqual(a, m.blue.teams) && m.blue.winner) {
                        wins++;
                    }
                }
            }
            if (wins == 2) wonSet.put(a, true);
        }

        List<MatchSet> wonSets = new ArrayList<>();
        List<MatchSet> sets = MatchSet.getSets(matches);
        for (MatchSet s : new ArrayList<>(sets)) {
            if (s.getWinner() != null) {
                sets.remove(s);
            }
        }
        Utils.log(sets.size() + " set(s) are not complete");
        if (sets.isEmpty()) { //This round is complete
            if (remainingAlliances.length > 2) { //we're in quarters or semis
                int[][] newAll = new int[remainingAlliances.length / 2][alliances[0].length];

                if (newAll.length == 4) { //ending quarterfinals
                    newAll[0] = wonSet.get(getAlliance(1)) ? getAlliance(1) : getAlliance(8);
                    newAll[1] = wonSet.get(seeds.get(2)) ? getAlliance(2) : getAlliance(7);
                    newAll[2] = wonSet.get(seeds.get(3)) ? getAlliance(3) : getAlliance(6);
                    newAll[3] = wonSet.get(seeds.get(4)) ? getAlliance(4) : getAlliance(5);
                } else if (newAll.length == 2) { //ending semifinals
                    Utils.log("Ending semis");
                    if (remainingAlliances == null) Utils.log("remaining is null");
                    if (newAll == null) Utils.log("newALl is null");
                    if (wonSet == null) Utils.log("wonset is null");
                    Utils.log(remainingAlliances[0] + "");
                    Utils.log(remainingAlliances[1] + "");
                    Utils.log(remainingAlliances[2] + "");
                    Utils.log(remainingAlliances[3] + "");
                    //Utils.log(remainingAlliances[0][0] + " is captain of 1");
                    //Utils.log(remainingAlliances[1][0] + " is captain of 2");
                    newAll[0] = wonSet.get(remainingAlliances[0]) ? remainingAlliances[0] : remainingAlliances[3];
                    newAll[1] = wonSet.get(remainingAlliances[1]) ? remainingAlliances[1] : remainingAlliances[2];
                }
                remainingAlliances = newAll;

                Gson g = new GsonBuilder().setPrettyPrinting().create();
                Utils.writeFile(Main.eventKey + "/remainingAlliances.json", g.toJson(remainingAlliances));

                generateNextElimMatches();
                Main.getInstance().gameField.updateMatchInfo();
            } else {
                //TODO: properly end the game
                Utils.log((wonSet.get(remainingAlliances[0]) ? arrayToString(remainingAlliances[0]) : arrayToString(remainingAlliances[1])) + " wins!");
            }
        } else {
            for (MatchSet s : sets) {
                Utils.log("Generating a set tiebreaker...");
                Match sample = s.getMatches().get(0);
                Match m = new Match(s.getMatches().size() + (sample.getLevel().equals("f") ? 1 : -1), sample.blue.teams, sample.red.teams).setLevel(sample.getLevel());
                m.tiebreaker = true;
                m.save();
                matches.add(m);
            }
            Main.getInstance().gameField.updateMatchInfo();
        }
    }

    public String arrayToString(int[] a) {
        String s = "";
        for (int i : a) {
            s += i + " - ";
        }
        s = s.substring(0, s.length()-3);
        return s;
    }

    public void generateNextElimMatches() {
        matches.clear();
        current = 0;
        for (int i=0; i<2; i++) {
            int roundAdd = i*(remainingAlliances.length/2);

            if (remainingAlliances.length == 2) {
                matches.add(new Match(1 + roundAdd, remainingAlliances[1], remainingAlliances[0]).setFinal());
            } else if (remainingAlliances.length == 4) {
                matches.add(new Match(1 + roundAdd, remainingAlliances[3], remainingAlliances[0]).setSemifinal());
                matches.add(new Match(2 + roundAdd, remainingAlliances[2], remainingAlliances[1]).setSemifinal());
            } else if (remainingAlliances.length == 8) {
                matches.add(new Match(1 + roundAdd, remainingAlliances[7], remainingAlliances[0]).setQuarterfinal());
                matches.add(new Match(2 + roundAdd, remainingAlliances[6], remainingAlliances[1]).setQuarterfinal());
                matches.add(new Match(3 + roundAdd, remainingAlliances[5], remainingAlliances[2]).setQuarterfinal());
                matches.add(new Match(4 + roundAdd, remainingAlliances[4], remainingAlliances[3]).setQuarterfinal());
            }
        }
        for (Match m : matches) {
            m.save();
        }
    }

    public int[] getAlliance(int seed) {
        return convert(seeds.get(seed));
    }

    public int[] convert(List<Double> a) {
        int[] converted = new int[a.size()];
        for (int i=0; i<a.size(); i++) {
            converted[i] = (int) Math.round(a.get(i));
        }
        return converted;
    }

    public int getSeed(int[] a) {
        int seed = 1;
        for (List<Double> b : seeds.values()) {
            if (Arrays.equals(a, convert(b))) {
                return seed;
            }
            seed++;
        }
        return -1;
    }

    public boolean arraysEqual(int[] a, int[] b) {
        for (int i : b) {
            if (!contains(a, i)) return false;
        }
        return true;
    }

    private boolean contains(int[] a, int i) {
        for (int f : a) {
            if (f == i) return true;
        }
        return false;
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

    //-1 = tie, 0 = blue, 1 = red
    public void completeCurrentMatch(int blueScore, int redScore, Object blueBreakdown, Object redBreakdown, int winner) {
        completeMatch(current, blueScore, redScore, blueBreakdown, redBreakdown, winner);
    }

    public void completeMatch(int index, int blueScore, int redScore, Object blueBreakdown, Object redBreakdown, int winner) {
        Match m = getMatch(index);

        m.blue.score = blueScore;
        m.blue.winner = winner == 0;
        m.blue.breakdown = blueBreakdown;

        m.red.score = redScore;
        m.red.winner = winner == 1;
        m.red.breakdown = redBreakdown;

        m.complete = true;

        if (Main.makeSchedule) {
            m.save();
            r.calculate();
        } else {
            Match newMatch = new Match(current+2, m.blue.teams, m.red.teams);
            newMatch.qualifier = false;
            matches.add(newMatch);
        }

        current++;
    }

    public List<Team> getTeams() {
        return new ArrayList<>(teams);
    }

    public Rankings getRankings() {
        return r;
    }
}

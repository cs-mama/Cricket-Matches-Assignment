import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class CricketMatchesAssignment {

    private static final String API_URL = "https://api.cuvora.com/car/partner/cricket-data";
    private static final String API_KEY = "test-creds@2320";

    public static void main(String[] args) {
        try {
            // Fetching the data from the API
            String jsonResponse = fetchCricketData();
            
            // Parsing the JSON response
            JSONArray matchesArray = new JSONArray(jsonResponse);

            // Variables to store results
            int highestScore = 0;
            String highestScoreTeam = "";
            int matchesWith300PlusScore = 0;

            // Looping through the matches
            for (int i = 0; i < matchesArray.length(); i++) {
                JSONObject match = matchesArray.getJSONObject(i);

                // Get scores for both teams
                String t1s = match.optString("t1s");
                String t2s = match.optString("t2s");

                // Convert scores to integers if they exist
                int t1Score = parseScore(t1s);
                int t2Score = parseScore(t2s);

                // Checking for highest score
                if (t1Score > highestScore) {
                    highestScore = t1Score;
                    highestScoreTeam = match.optString("t1");
                }
                if (t2Score > highestScore) {
                    highestScore = t2Score;
                    highestScoreTeam = match.optString("t2");
                }

                // Check if the match has a combined score of more than 300
                if (t1Score + t2Score > 300) {
                    matchesWith300PlusScore++;
                }
            }

            // Print results
            System.out.println("Highest Score: " + highestScore + " and Team Name is: " + highestScoreTeam);
            System.out.println("Number Of Matches with total 300 Plus Score: " + matchesWith300PlusScore);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to fetch cricket data from the API
    private static String fetchCricketData() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("apiKey", API_KEY);

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) { // Success
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return content.toString();
        } else {
            throw new Exception("Failed to fetch data: HTTP error code " + responseCode);
        }
    }

    // Method to parse the score from the score string
    private static int parseScore(String score) {
        if (score == null || score.isEmpty()) {
            return 0;
        }
        String[] parts = score.split("/");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

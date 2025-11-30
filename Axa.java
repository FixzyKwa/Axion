import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Axa {

    private static String mojangApi = "https://api.mojang.com/users/profiles/minecraft/";

    public static boolean validate(String uuid, String nickname) {
        try {
            URL url = new URL(mojangApi + nickname);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                return false;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String response = br.readLine();
            br.close();

            JSONObject json = new JSONObject(response);
            String apiUUID = json.getString("id");

            String cleanUUID = uuid.replace("-", ""); // Tireleri temizle

            return apiUUID.equalsIgnoreCase(cleanUUID);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Oyunu kapatan Auth metodu
    public static void Auth(String uuid, String nickname) {
        if (validate(uuid, nickname)) {
            System.out.println("[AxA] Auth Successful!");
        } else {
            System.out.println("[AxA] Auth Failed! Game will shut down...");
            shutdownGame();
        }
    }

    // Programı tamamen kapatır
    private static void shutdownGame() {
        try {
            Thread.sleep(1500); // Mesajı göstermesi için 1.5 saniye bekle
        } catch (InterruptedException ignored) {}

        System.exit(0); // Oyunu kapat
    }
}
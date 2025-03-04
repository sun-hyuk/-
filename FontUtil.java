package Project;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class FontUtil {
	private static Font googleFont;

    public static Font getGoogleFont(float size) {
    	if (googleFont == null) {
            try (InputStream is = FontUtil.class.getResourceAsStream("/Project/Font/CuteFont-Regular.ttf")) {
                if (is == null) {
                    System.out.println("폰트 파일을 찾을 수 없습니다.");
                    // 기본 폰트 반환
                    return new Font("SansSerif", Font.PLAIN, (int) size);
                }
                googleFont = Font.createFont(Font.TRUETYPE_FONT, is);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                googleFont = new Font("SansSerif", Font.PLAIN, (int) size);
            }
        }
        return googleFont.deriveFont(size);
    }
}

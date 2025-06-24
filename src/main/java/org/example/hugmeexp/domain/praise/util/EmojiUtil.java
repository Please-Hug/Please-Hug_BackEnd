package org.example.hugmeexp.domain.praise.util;

public class EmojiUtil {

    public static boolean isOnlyEmoji(String input) {
        if (input == null || input.isBlank()) return false;

        int[] codePoints = input.codePoints().toArray();
        for (int cp : codePoints) {
            if (!isEmojiRelated(cp)) return false;
        }
        return true;
    }

    private static boolean isEmojiRelated(int codePoint) {
        return (codePoint >= 0x1F600 && codePoint <= 0x1F64F)   // Emoticons
                || (codePoint >= 0x1F300 && codePoint <= 0x1F5FF)   // Misc Symbols and Pictographs
                || (codePoint >= 0x1F680 && codePoint <= 0x1F6FF)   // Transport and Map
                || (codePoint >= 0x1F1E6 && codePoint <= 0x1F1FF)   // Regional Indicator
                || (codePoint >= 0x2600 && codePoint <= 0x26FF)     // Misc symbols
                || (codePoint >= 0x2700 && codePoint <= 0x27BF)     // Dingbats
                || (codePoint >= 0xFE00 && codePoint <= 0xFE0F)     // Variation Selectors
                || (codePoint >= 0x1F900 && codePoint <= 0x1F9FF)   // Supplemental Symbols
                || (codePoint >= 0x1FA70 && codePoint <= 0x1FAFF)   // Extended-A
                || (codePoint >= 0x1F3FB && codePoint <= 0x1F3FF)   // Skin tone
                || (codePoint == 0x200D)                            // ZWJ
                || (codePoint == 0x20E3)                            // Keycap
                || (codePoint == 0x1F004)                           // Mahjong
                || (codePoint == 0x1F0CF)                           // Playing card
                || (codePoint == 0x1F18E)
                || (codePoint >= 0x1F191 && codePoint <= 0x1F19A)
                || (codePoint >= 0x1F201 && codePoint <= 0x1F202)
                || (codePoint == 0x1F21A)
                || (codePoint >= 0x1F22F && codePoint <= 0x1F23A)
                || (codePoint >= 0x1F250 && codePoint <= 0x1F251);
    }

    private EmojiUtil() {
        throw new UnsupportedOperationException("유틸 클래스는 인스턴스화할 수 없습니다.");
    }
}

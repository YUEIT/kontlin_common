package cn.yue.base.common.widget.emoji;

import android.content.Context;
import android.text.Spannable;
import android.util.SparseIntArray;

import cn.yue.base.common.R;


public final class EmojiconHandler {
    private EmojiconHandler() {
    }

    private static final SparseIntArray sEmojisMap = new SparseIntArray();
    private static final SparseIntArray sSoftbanksMap = new SparseIntArray();


    static {
        sEmojisMap.put(0x1f642, R.drawable.emoji_1f642);
        sEmojisMap.put(0x1f616, R.drawable.emoji_1f616);
        sEmojisMap.put(0x1f60d, R.drawable.emoji_1f60d);
        sEmojisMap.put(0x1f62e, R.drawable.emoji_1f62e);
        sEmojisMap.put(0x1f60e, R.drawable.emoji_1f60e);
        sEmojisMap.put(0x1f62d, R.drawable.emoji_1f62d);
        sEmojisMap.put(0x1f914, R.drawable.emoji_1f914);
        sEmojisMap.put(0x1f910, R.drawable.emoji_1f910);
        sEmojisMap.put(0x1f62a, R.drawable.emoji_1f62a);
        sEmojisMap.put(0x1f629, R.drawable.emoji_1f629);
        sEmojisMap.put(0x1f628, R.drawable.emoji_1f628);
        sEmojisMap.put(0x1f621, R.drawable.emoji_1f621);
        sEmojisMap.put(0x1f61b, R.drawable.emoji_1f61b);
        sEmojisMap.put(0x1f62c, R.drawable.emoji_1f62c);
        sEmojisMap.put(0x1f627, R.drawable.emoji_1f627);
        sEmojisMap.put(0x1f641, R.drawable.emoji_1f641);
        sEmojisMap.put(0x1f624, R.drawable.emoji_1f624);
        sEmojisMap.put(0x1f61e, R.drawable.emoji_1f61e);
        sEmojisMap.put(0x1f60a, R.drawable.emoji_1f60a);
        sEmojisMap.put(0x1f644, R.drawable.emoji_1f644);
        sEmojisMap.put(0x1f60b, R.drawable.emoji_1f60b);
        sEmojisMap.put(0x1f635, R.drawable.emoji_1f635);
        sEmojisMap.put(0x1f630, R.drawable.emoji_1f630);
        sEmojisMap.put(0x1f613, R.drawable.emoji_1f613);
        sEmojisMap.put(0x1f603, R.drawable.emoji_1f603);
        sEmojisMap.put(0x1f608, R.drawable.emoji_1f608);
        sEmojisMap.put(0x1f913, R.drawable.emoji_1f913);
        sEmojisMap.put(0x1f632, R.drawable.emoji_1f632);
        sEmojisMap.put(0x1f912, R.drawable.emoji_1f912);
        sEmojisMap.put(0x1f635, R.drawable.emoji_1f635);
        sEmojisMap.put(0x1f622, R.drawable.emoji_1f622);
        sEmojisMap.put(0x1f61c, R.drawable.emoji_1f61c);
        sEmojisMap.put(0x1f61a, R.drawable.emoji_1f61a);
        sEmojisMap.put(0x1f636, R.drawable.emoji_1f636);
        sEmojisMap.put(0x1f917, R.drawable.emoji_1f917);
        sEmojisMap.put(0x1f609, R.drawable.emoji_1f609);
        sEmojisMap.put(0x1f601, R.drawable.emoji_1f601);
        sEmojisMap.put(0x1f637, R.drawable.emoji_1f637);
        sEmojisMap.put(0x1f602, R.drawable.emoji_1f602);
        sEmojisMap.put(0x1f61d, R.drawable.emoji_1f61d);
        sEmojisMap.put(0x1f633, R.drawable.emoji_1f633);
        sEmojisMap.put(0x1f631, R.drawable.emoji_1f631);
        sEmojisMap.put(0x1f614, R.drawable.emoji_1f614);
        sEmojisMap.put(0x1f612, R.drawable.emoji_1f612);
        sEmojisMap.put(0x1f60c, R.drawable.emoji_1f60c);
        sEmojisMap.put(0x1f60f, R.drawable.emoji_1f60f);
        sEmojisMap.put(0x1f643, R.drawable.emoji_1f643);
        sEmojisMap.put(0x1f47d, R.drawable.emoji_1f47d);
        sEmojisMap.put(0x1f47b, R.drawable.emoji_1f47b);
        sEmojisMap.put(0x1f480, R.drawable.emoji_1f480);
        sEmojisMap.put(0x1f31a, R.drawable.emoji_1f31a);
        sEmojisMap.put(0x1f31d, R.drawable.emoji_1f31d);
        sEmojisMap.put(0x1f4a4, R.drawable.emoji_1f4a4);
        sEmojisMap.put(0x1f31e, R.drawable.emoji_1f31e);
        sEmojisMap.put(0x1f647, R.drawable.emoji_1f647);
        sEmojisMap.put(0x1f64b, R.drawable.emoji_1f64b);
        sEmojisMap.put(0x1f646, R.drawable.emoji_1f646);
        sEmojisMap.put(0x1f47e, R.drawable.emoji_1f47e);
        sEmojisMap.put(0x1f52a, R.drawable.emoji_1f52a);
        sEmojisMap.put(0x1f349, R.drawable.emoji_1f349);
        sEmojisMap.put(0x1f37b, R.drawable.emoji_1f37b);
        sEmojisMap.put(0x2615, R.drawable.emoji_2615);
        sEmojisMap.put(0x1f437, R.drawable.emoji_1f437);
        sEmojisMap.put(0x1f339, R.drawable.emoji_1f339);
        sEmojisMap.put(0x1f48b, R.drawable.emoji_1f48b);
        sEmojisMap.put(0x2764, R.drawable.emoji_2764);
        sEmojisMap.put(0x1f494, R.drawable.emoji_1f494);
        sEmojisMap.put(0x1f382, R.drawable.emoji_1f382);
        sEmojisMap.put(0x1f4a3, R.drawable.emoji_1f4a3);
        sEmojisMap.put(0x1f4a9, R.drawable.emoji_1f4a9);
        sEmojisMap.put(0x1f469, R.drawable.emoji_1f469);
        sEmojisMap.put(0x1f595, R.drawable.emoji_1f595);
        sEmojisMap.put(0x1f44d, R.drawable.emoji_1f44d);
        sEmojisMap.put(0x1f44e, R.drawable.emoji_1f44e);
        sEmojisMap.put(0x1f44f, R.drawable.emoji_1f44f);
        sEmojisMap.put(0x270c, R.drawable.emoji_270c);
        sEmojisMap.put(0x1f918, R.drawable.emoji_1f918);
        sEmojisMap.put(0x1f44a, R.drawable.emoji_1f44a);
        sEmojisMap.put(0x1f44c, R.drawable.emoji_1f44c);
        sEmojisMap.put(0x1f44b, R.drawable.emoji_1f44b);
        sEmojisMap.put(0x261d, R.drawable.emoji_261d);
        sEmojisMap.put(0x1f4aa, R.drawable.emoji_1f4aa);
        sEmojisMap.put(0x1f64f, R.drawable.emoji_1f64f);
        sEmojisMap.put(0x1f388, R.drawable.emoji_1f388);
        sEmojisMap.put(0x1f445, R.drawable.emoji_1f445);
        sEmojisMap.put(0x1f389, R.drawable.emoji_1f389);
        sEmojisMap.put(0x1f381, R.drawable.emoji_1f381);
        sEmojisMap.put(0x1f436, R.drawable.emoji_1f436);
        sEmojisMap.put(0x1f4b0, R.drawable.emoji_1f4b0);
        sEmojisMap.put(0x1f3b5, R.drawable.emoji_1f3b5);
        sEmojisMap.put(0x1f451, R.drawable.emoji_1f451);
        // People
        /*sEmojisMap.put(0x1f604, R.drawable.emoji_1f604);
        sEmojisMap.put(0x1f603, R.drawable.emoji_1f603);
        sEmojisMap.put(0x1f60a, R.drawable.emoji_1f60a);
        sEmojisMap.put(0x1f609, R.drawable.emoji_1f609);
        sEmojisMap.put(0x1f60d, R.drawable.emoji_1f60d);
        sEmojisMap.put(0x1f618, R.drawable.emoji_1f618);
        sEmojisMap.put(0x1f61a, R.drawable.emoji_1f61a);
        sEmojisMap.put(0x1f61c, R.drawable.emoji_1f61c);
        sEmojisMap.put(0x1f61d, R.drawable.emoji_1f61d);
        sEmojisMap.put(0x1f633, R.drawable.emoji_1f633);
        sEmojisMap.put(0x1f601, R.drawable.emoji_1f601);
        sEmojisMap.put(0x1f614, R.drawable.emoji_1f614);
        sEmojisMap.put(0x1f60c, R.drawable.emoji_1f60c);
        sEmojisMap.put(0x1f61e, R.drawable.emoji_1f61e);
        sEmojisMap.put(0x1f623, R.drawable.emoji_1f623);
        sEmojisMap.put(0x1f622, R.drawable.emoji_1f622);
        sEmojisMap.put(0x1f602, R.drawable.emoji_1f602);
        sEmojisMap.put(0x1f62d, R.drawable.emoji_1f62d);
        sEmojisMap.put(0x1f62a, R.drawable.emoji_1f62a);
        sEmojisMap.put(0x1f625, R.drawable.emoji_1f625);
        sEmojisMap.put(0x1f630, R.drawable.emoji_1f630);
        sEmojisMap.put(0x1f613, R.drawable.emoji_1f613);
        sEmojisMap.put(0x1f628, R.drawable.emoji_1f628);
        sEmojisMap.put(0x1f631, R.drawable.emoji_1f631);
        sEmojisMap.put(0x1f621, R.drawable.emoji_1f621);
        sEmojisMap.put(0x1f616, R.drawable.emoji_1f616);
        sEmojisMap.put(0x1f637, R.drawable.emoji_1f637);
        sEmojisMap.put(0x1f60e, R.drawable.emoji_1f60e);
        sEmojisMap.put(0x1f634, R.drawable.emoji_1f634);
        sEmojisMap.put(0x1f632, R.drawable.emoji_1f632);
        sEmojisMap.put(0x1f47f, R.drawable.emoji_1f47f);
        sEmojisMap.put(0x1f607, R.drawable.emoji_1f607);
        sEmojisMap.put(0x1f60f, R.drawable.emoji_1f60f);
        sEmojisMap.put(0x1f47c, R.drawable.emoji_1f47c);
        sEmojisMap.put(0x1f47d, R.drawable.emoji_1f47d);
        sEmojisMap.put(0x2728, R.drawable.emoji_2728);
        sEmojisMap.put(0x1f4a2, R.drawable.emoji_1f4a2);
        sEmojisMap.put(0x1f4a6, R.drawable.emoji_1f4a6);
        sEmojisMap.put(0x1f4a4, R.drawable.emoji_1f4a4);
        sEmojisMap.put(0x1f44d, R.drawable.emoji_1f44d);
        sEmojisMap.put(0x1f44c, R.drawable.emoji_1f44c);
        sEmojisMap.put(0x270a, R.drawable.emoji_270a);
        sEmojisMap.put(0x270c, R.drawable.emoji_270c);
        sEmojisMap.put(0x1f64f, R.drawable.emoji_1f64f);
        sEmojisMap.put(0x1f44f, R.drawable.emoji_1f44f);
        sEmojisMap.put(0x1f4aa, R.drawable.emoji_1f4aa);
        sEmojisMap.put(0x1f451, R.drawable.emoji_1f451);
        sEmojisMap.put(0x1f302, R.drawable.emoji_1f302);
        sEmojisMap.put(0x2764, R.drawable.emoji_2764);
        sEmojisMap.put(0x1f494, R.drawable.emoji_1f494);
        sEmojisMap.put(0x1f48b, R.drawable.emoji_1f48b);
        sEmojisMap.put(0x1f48d, R.drawable.emoji_1f48d);
        sEmojisMap.put(0x1f463, R.drawable.emoji_1f463);

        // Nature
        sEmojisMap.put(0x1f490, R.drawable.emoji_1f490);
        sEmojisMap.put(0x1f338, R.drawable.emoji_1f338);
        sEmojisMap.put(0x1f339, R.drawable.emoji_1f339);
        sEmojisMap.put(0x2b50, R.drawable.emoji_2b50);

        // Objects
        sEmojisMap.put(0x1f47b, R.drawable.emoji_1f47b);
        sEmojisMap.put(0x1f381, R.drawable.emoji_1f381);
        sEmojisMap.put(0x1f389, R.drawable.emoji_1f389);
        sEmojisMap.put(0x1f388, R.drawable.emoji_1f388);
        sEmojisMap.put(0x1f4e2, R.drawable.emoji_1f4e2);
        sEmojisMap.put(0x1f3a4, R.drawable.emoji_1f3a4);
        sEmojisMap.put(0x1f3b5, R.drawable.emoji_1f3b5);
        sEmojisMap.put(0x1f3b6, R.drawable.emoji_1f3b6);
        sEmojisMap.put(0x1f3ae, R.drawable.emoji_1f3ae);
        sEmojisMap.put(0x1f37b, R.drawable.emoji_1f37b);
        sEmojisMap.put(0x1f382, R.drawable.emoji_1f382);
        sEmojisMap.put(0x1f36d, R.drawable.emoji_1f36d);

        //社区特殊字符定制
        sEmojisMap.put(0x2f001, R.drawable.emoji_ff0011);
        sEmojisMap.put(0x2f002, R.drawable.emoji_ff0012);*/

        // Places
        sSoftbanksMap.put(0xe003, R.drawable.emoji_1f48b);
        sSoftbanksMap.put(0xe00e, R.drawable.emoji_1f44d);
        sSoftbanksMap.put(0xe010, R.drawable.emoji_270a);
        sSoftbanksMap.put(0xe011, R.drawable.emoji_270c);
        sSoftbanksMap.put(0xe022, R.drawable.emoji_2764);
        sSoftbanksMap.put(0xe023, R.drawable.emoji_1f494);
        sSoftbanksMap.put(0xe030, R.drawable.emoji_1f338);
        sSoftbanksMap.put(0xe032, R.drawable.emoji_1f339);
        sSoftbanksMap.put(0xe034, R.drawable.emoji_1f48d);
        sSoftbanksMap.put(0xe03c, R.drawable.emoji_1f3a4);
        sSoftbanksMap.put(0xe03e, R.drawable.emoji_1f3b5);
        sSoftbanksMap.put(0xe04e, R.drawable.emoji_1f47c);
        sSoftbanksMap.put(0xe056, R.drawable.emoji_1f60a);
        sSoftbanksMap.put(0xe057, R.drawable.emoji_1f603);
        sSoftbanksMap.put(0xe058, R.drawable.emoji_1f61e);
        sSoftbanksMap.put(0xe105, R.drawable.emoji_1f61c);
        sSoftbanksMap.put(0xe106, R.drawable.emoji_1f60d);
        sSoftbanksMap.put(0xe107, R.drawable.emoji_1f631);
        sSoftbanksMap.put(0xe108, R.drawable.emoji_1f613);
        sSoftbanksMap.put(0xe10c, R.drawable.emoji_1f47d);
        sSoftbanksMap.put(0xe10e, R.drawable.emoji_1f451);
        sSoftbanksMap.put(0xe112, R.drawable.emoji_1f381);
        sSoftbanksMap.put(0xe11a, R.drawable.emoji_1f47f);
        sSoftbanksMap.put(0xe11b, R.drawable.emoji_1f47b);
        sSoftbanksMap.put(0xe13c, R.drawable.emoji_1f4a4);
        sSoftbanksMap.put(0xe142, R.drawable.emoji_1f4e2);
        sSoftbanksMap.put(0xe14c, R.drawable.emoji_1f4aa);
        sSoftbanksMap.put(0xe306, R.drawable.emoji_1f490);
        sSoftbanksMap.put(0xe30c, R.drawable.emoji_1f37b);
        sSoftbanksMap.put(0xe310, R.drawable.emoji_1f388);
        sSoftbanksMap.put(0xe312, R.drawable.emoji_1f389);
        sSoftbanksMap.put(0xe326, R.drawable.emoji_1f3b6);
        sSoftbanksMap.put(0xe32e, R.drawable.emoji_2728);
        sSoftbanksMap.put(0xe32f, R.drawable.emoji_2b50);
        sSoftbanksMap.put(0xe331, R.drawable.emoji_1f4a6);
        sSoftbanksMap.put(0xe334, R.drawable.emoji_1f4a2);
        sSoftbanksMap.put(0xe34b, R.drawable.emoji_1f382);
        sSoftbanksMap.put(0xe401, R.drawable.emoji_1f625);
        sSoftbanksMap.put(0xe402, R.drawable.emoji_1f60f);
        sSoftbanksMap.put(0xe403, R.drawable.emoji_1f614);
        sSoftbanksMap.put(0xe404, R.drawable.emoji_1f601);
        sSoftbanksMap.put(0xe405, R.drawable.emoji_1f609);
        sSoftbanksMap.put(0xe406, R.drawable.emoji_1f623);
        sSoftbanksMap.put(0xe407, R.drawable.emoji_1f616);
        sSoftbanksMap.put(0xe408, R.drawable.emoji_1f62a);
        sSoftbanksMap.put(0xe40b, R.drawable.emoji_1f628);
        sSoftbanksMap.put(0xe40c, R.drawable.emoji_1f637);
        sSoftbanksMap.put(0xe40d, R.drawable.emoji_1f633);
        sSoftbanksMap.put(0xe40f, R.drawable.emoji_1f630);
        sSoftbanksMap.put(0xe410, R.drawable.emoji_1f632);
        sSoftbanksMap.put(0xe411, R.drawable.emoji_1f62d);
        sSoftbanksMap.put(0xe412, R.drawable.emoji_1f602);
        sSoftbanksMap.put(0xe413, R.drawable.emoji_1f622);
        sSoftbanksMap.put(0xe416, R.drawable.emoji_1f621);
        sSoftbanksMap.put(0xe417, R.drawable.emoji_1f61a);
        sSoftbanksMap.put(0xe418, R.drawable.emoji_1f618);
        sSoftbanksMap.put(0xe41d, R.drawable.emoji_1f64f);
        sSoftbanksMap.put(0xe41f, R.drawable.emoji_1f44f);
        sSoftbanksMap.put(0xe420, R.drawable.emoji_1f44c);
        sSoftbanksMap.put(0xe43c, R.drawable.emoji_1f302);
        sSoftbanksMap.put(0xe513, R.drawable.emoji_1f1e8_1f1f3);
    }

    private static boolean isSoftBankEmoji(char c) {
        return ((c >> 12) == 0xe);
    }

    private static int getEmojiResource(Context context, int codePoint) {
        return sEmojisMap.get(codePoint);
    }

    private static int getSoftbankEmojiResource(char c) {
        return sSoftbanksMap.get(c);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int textSize) {
        addEmojis(context, text, emojiSize, textSize, 0, -1, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int textSize, int index, int length) {
        addEmojis(context, text, emojiSize, textSize, index, length, false);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int textSize, boolean useSystemDefault) {
        addEmojis(context, text, emojiSize, textSize, 0, -1, useSystemDefault);
    }

    /**
     * Convert emoji characters of the given Spannable to the according emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     * @param index
     * @param length
     * @param useSystemDefault
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize, int textSize, int index, int length, boolean useSystemDefault) {
        if (useSystemDefault) {
            return;
        }

        int textLength = text.length();
        int textLengthToProcessMax = textLength - index;
        int textLengthToProcess = length < 0 || length >= textLengthToProcessMax ? textLength : (length + index);

        // remove spans throughout all text
        EmojiconSpan[] oldSpans = text.getSpans(0, textLength, EmojiconSpan.class);
        for (int i = 0; i < oldSpans.length; i++) {
            text.removeSpan(oldSpans[i]);
        }

        int skip;
        for (int i = index; i < textLengthToProcess; i += skip) {
            skip = 0;
            int icon = 0;
            char c = text.charAt(i);
            if (isSoftBankEmoji(c)) {
                icon = getSoftbankEmojiResource(c);
                skip = icon == 0 ? 0 : 1;
            }

            if (icon == 0) {
                int unicode = Character.codePointAt(text, i);
                skip = Character.charCount(unicode);

                if (unicode > 0xff) {
                    icon = getEmojiResource(context, unicode);
                }

                if (icon == 0 && i + skip < textLengthToProcess) {
                    int followUnicode = Character.codePointAt(text, i + skip);
                    if (followUnicode == 0x20e3) {
                        int followSkip = Character.charCount(followUnicode);
                        switch (unicode) {
                            default:
                                followSkip = 0;
                                break;
                        }
                        skip += followSkip;
                    } else {
                        int followSkip = Character.charCount(followUnicode);
                        switch (unicode) {
                            case 0x1f1e8:
                                icon = (followUnicode == 0x1f1f3) ? R.drawable.emoji_1f1e8_1f1f3 : 0;
                                break;
                            default:
                                followSkip = 0;
                                break;
                        }
                        skip += followSkip;
                    }
                }
            }

            if (icon > 0) {
                text.setSpan(new EmojiconSpan(context, icon, emojiSize, textSize), i, i + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    public static int getEmojiResId(int emoji, int notfoundId) {
        int resId = sEmojisMap.get(emoji, notfoundId);
        if (resId != notfoundId) {
            return resId;
        } else {
            resId = sSoftbanksMap.get(emoji, notfoundId);
            return resId;
        }

    }
}
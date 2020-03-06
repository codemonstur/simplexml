package xmlparser.utils;

import java.util.Map;

import static xmlparser.utils.Constants.*;

public enum Escaping {;

    public interface UnEscape {
        String unescape(String input);
    }

    public static String unescapeXml(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        int n = text.length();
        while (i < n) {
            char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
            } else {
                if (text.startsWith(ENCODED_AMPERSAND, i)) {
                    result.append(CHAR_AMPERSAND);
                    i += 5;
                } else if (text.startsWith(ENCODED_SINGLE_QUOTE, i)) {
                    result.append(CHAR_SINGLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_DOUBLE_QUOTE, i)) {
                    result.append(CHAR_DOUBLE_QUOTE);
                    i += 6;
                } else if (text.startsWith(ENCODED_LESS_THAN, i)) {
                    result.append(CHAR_LESS_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_GREATER_THAN, i)) {
                    result.append(CHAR_GREATER_THAN);
                    i += 4;
                } else if (text.startsWith(ENCODED_UTF8, i)) {
                    final int index = text.indexOf(';', i);
                    result.append(charFromDecimal(text.substring(i+2, index)));
                    i = index+1;
                }
                else i++;
            }
        }
        return result.toString();
    }

    // https://www.freeformatter.com/html-entities.html
    private static final Map<String, String> NAMED_HTML_ENTITIES = Builder.<String, String>newHashMap()
        .put("&amp;", "&").put("&lt;", "<").put("&gt;", ">").put("&Agrave;", "À")
        .put("&Aacute;", "Á").put("&Acirc;", "Â").put("&Atilde;", "Ã").put("&Auml;", "Ä")
        .put("&Aring;", "Å").put("&AElig;", "Æ").put("&Ccedil;", "Ç").put("&Egrave;", "È")
        .put("&Eacute;", "É").put("&Ecirc;", "Ê").put("&Euml;", "Ë").put("&Igrave;", "Ì")
        .put("&Iacute;", "Í").put("&Icirc;", "Î").put("&Iuml;", "Ï").put("&ETH;", "Ð")
        .put("&Ntilde;", "Ñ").put("&Ograve;", "Ò").put("&Oacute;", "Ó").put("&Ocirc;", "Ô")
        .put("&Otilde;", "Õ").put("&Ouml;", "Ö").put("&Oslash;", "Ø").put("&Ugrave;", "Ù")
        .put("&Uacute;", "Ú").put("&Ucirc;", "Û").put("&Uuml;", "Ü").put("&Yacute;", "Ý")
        .put("&THORN;", "Þ").put("&szlig;", "ß").put("&agrave;", "à").put("&aacute;", "á")
        .put("&acirc;", "â").put("&atilde;", "ã").put("&auml;", "ä").put("&aring;", "å")
        .put("&aelig;", "æ").put("&ccedil;", "ç").put("&egrave;", "è").put("&eacute;", "é")
        .put("&ecirc;", "ê").put("&euml;", "ë").put("&igrave;", "ì").put("&iacute;", "í")
        .put("&icirc;", "î").put("&iuml;", "ï").put("&eth;", "ð").put("&ntilde;", "ñ")
        .put("&ograve;", "ò").put("&oacute;", "ó").put("&ocirc;", "ô").put("&otilde;", "õ")
        .put("&ouml;", "ö").put("&oslash;", "ø").put("&ugrave;", "ù").put("&uacute;", "ú")
        .put("&ucirc;", "û").put("&uuml;", "ü").put("&yacute;", "ý").put("&thorn;", "þ")
        .put("&yuml;", "ÿ").put("&nbsp;", " ").put("&iexcl;", "¡").put("&cent;", "¢")
        .put("&pound;", "£").put("&curren;", "¤").put("&yen;", "¥").put("&brvbar;", "¦")
        .put("&sect;", "§").put("&uml;", "¨").put("&copy;", "©").put("&ordf;", "ª")
        .put("&laquo;", "«").put("&not;", "¬").put("&shy;", "\u00ad").put("&reg;", "®")
        .put("&macr;", "¯").put("&deg;", "°").put("&plusmn;", "±").put("&sup2;", "²")
        .put("&sup3;", "³").put("&acute;", "´").put("&micro;", "µ").put("&para;", "¶")
        .put("&cedil;", "¸").put("&sup1;", "¹").put("&ordm;", "º").put("&raquo;", "»")
        .put("&frac14;", "¼").put("&frac12;", "½").put("&frac34;", "¾").put("&iquest;", "¿")
        .put("&times;", "×").put("&divide;", "÷").put("&forall;", "∀").put("&part;", "∂")
        .put("&exist;", "∃").put("&empty;", "∅").put("&nabla;", "∇").put("&isin;", "∈")
        .put("&notin;", "∉").put("&ni;", "∋").put("&prod;", "∏").put("&sum;", "∑")
        .put("&minus;", "−").put("&lowast;", "∗").put("&radic;", "√").put("&prop;", "∝")
        .put("&infin;", "∞").put("&ang;", "∠").put("&and;", "∧").put("&or;", "∨")
        .put("&cap;", "∩").put("&cup;", "∪").put("&int;", "∫").put("&there4;", "∴")
        .put("&sim;", "∼").put("&cong;", "≅").put("&asymp;", "≈").put("&ne;", "≠")
        .put("&equiv;", "≡").put("&le;", "≤").put("&ge;", "≥").put("&sub;", "⊂")
        .put("&sup;", "⊃").put("&nsub;", "⊄").put("&sube;", "⊆").put("&supe;", "⊇")
        .put("&oplus;", "⊕").put("&otimes;", "⊗").put("&perp;", "⊥").put("&sdot;", "⋅")
        .put("&Alpha;", "Α").put("&Beta;", "Β").put("&Gamma;", "Γ").put("&Delta;", "Δ")
        .put("&Epsilon;", "Ε").put("&Zeta;", "Ζ").put("&Eta;", "Η").put("&Theta;", "Θ")
        .put("&Iota;", "Ι").put("&Kappa;", "Κ").put("&Lambda;", "Λ").put("&Mu;", "Μ")
        .put("&Nu;", "Ν").put("&Xi;", "Ξ").put("&Omicron;", "Ο").put("&Pi;", "Π")
        .put("&Rho;", "Ρ").put("&Sigma;", "Σ").put("&Tau;", "Τ").put("&Upsilon;", "Υ")
        .put("&Phi;", "Φ").put("&Chi;", "Χ").put("&Psi;", "Ψ").put("&Omega;", "Ω")
        .put("&alpha;", "α").put("&beta;", "β").put("&gamma;", "γ").put("&delta;", "δ")
        .put("&epsilon;", "ε").put("&zeta;", "ζ").put("&eta;", "η").put("&theta;", "θ")
        .put("&iota;", "ι").put("&kappa;", "κ").put("&lambda;", "λ").put("&mu;", "μ")
        .put("&nu;", "ν").put("&xi;", "ξ").put("&omicron;", "ο").put("&pi;", "π")
        .put("&rho;", "ρ").put("&sigmaf;", "ς").put("&sigma;", "σ").put("&tau;", "τ")
        .put("&upsilon;", "υ").put("&phi;", "φ").put("&chi;", "χ").put("&psi;", "ψ")
        .put("&omega;", "ω").put("&thetasym;", "ϑ").put("&upsih;", "ϒ").put("&piv;", "ϖ")
        .put("&OElig;", "Œ").put("&oelig;", "œ").put("&Scaron;", "Š").put("&scaron;", "š")
        .put("&Yuml;", "Ÿ").put("&fnof;", "ƒ").put("&circ;", "ˆ").put("&tilde;", "˜")
        .put("&ensp;", "\u2002").put("&emsp;", "\u2003").put("&thinsp;", "\u2009").put("&zwnj;", "\u200c")
        .put("&zwj;", "\u200d").put("&lrm;", "\u200e").put("&rlm;", "\u200f").put("&ndash;", "–")
        .put("&mdash;", "—").put("&lsquo;", "‘").put("&rsquo;", "’").put("&sbquo;", "‚")
        .put("&ldquo;", "“").put("&rdquo;", "”").put("&bdquo;", "„").put("&dagger;", "†")
        .put("&Dagger;", "‡").put("&bull;", "•").put("&hellip;", "…").put("&permil;", "‰")
        .put("&prime;", "′").put("&Prime;", "″").put("&lsaquo;", "‹").put("&rsaquo;", "›")
        .put("&oline;", "‾").put("&euro;", "€").put("&trade;", "™").put("&larr;", "←")
        .put("&uarr;", "↑").put("&rarr;", "→").put("&darr;", "↓").put("&harr;", "↔")
        .put("&crarr;", "↵").put("&lceil;", "⌈").put("&rceil;", "⌉").put("&lfloor;", "⌊")
        .put("&rfloor;", "⌋").put("&loz;", "◊").put("&spades;", "♠").put("&clubs;", "♣")
        .put("&hearts;", "♥").put("&diams;", "♦").build();

    public static String unescapeHtml(final String text) {
        final StringBuilder result = new StringBuilder(text.length());
        int i = 0;
        while (i < text.length()) {
            final char charAt = text.charAt(i);
            if (charAt != CHAR_AMPERSAND) {
                result.append(charAt);
                i++;
                continue;
            }

            final int index = text.indexOf(';', i);
            if (index == -1) {
                result.append("&");
                i++;
                continue;
            }

            final String entity = text.substring(i, index+1);
            final String decode = NAMED_HTML_ENTITIES.get(entity);
            if (decode != null) {
                result.append(decode);
                i = index+1;
                continue;
            }

            if (text.charAt(i+1) == '#') {
                final char real = text.charAt(i+2) == 'x'
                        ? charFromHex(text.substring(i+3, index))
                        : charFromDecimal(text.substring(i+2, index));
                result.append(real);
                i = index+1;
                continue;
            }

            result.append("&");
            i++;
        }
        return result.toString();
    }

    private static char charFromDecimal(final String substring) {
        return (char) Integer.parseInt(substring);
    }
    private static char charFromHex(final String substring) {
        return (char) Integer.parseInt(substring, 16);
    }

}

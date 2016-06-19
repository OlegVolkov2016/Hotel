package com.axiomsl.hotel.configuration;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * URL Encoding utility
 */
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public class UrlUtil {
    public static String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String encoding = httpServletRequest.getCharacterEncoding();
        if (encoding == null) {
            encoding = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, encoding);
        } catch (UnsupportedEncodingException e) {
        }
        return pathSegment;
    }
}

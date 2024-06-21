package com.jet.com.secureappback.utils;

import static com.jet.com.secureappback.utils.SecureAppBackApplicationConst.USER_AGENT_HEADER;
import static com.jet.com.secureappback.utils.SecureAppBackApplicationConst.X_FORWARDED_FOR_HEADER;
import static nl.basjes.parse.useragent.UserAgent.AGENT_NAME;
import static nl.basjes.parse.useragent.UserAgent.DEVICE_NAME;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 12/06/2024
 */
public class RequestUtils {

  private RequestUtils() {}

  public static String getIpAddress(HttpServletRequest request) {
    String ipAddress = "Unknown IP";
    if (request != null) {
      ipAddress = request.getHeader(X_FORWARDED_FOR_HEADER);
      if (ipAddress == null || "".equals(ipAddress)) {
        ipAddress = request.getRemoteAddr();
      }
    }
    return ipAddress;
  }

  public static String getDevice(HttpServletRequest request) {
    UserAgentAnalyzer userAgentAnalyzer =
        UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
    UserAgent agent = userAgentAnalyzer.parse(request.getHeader(USER_AGENT_HEADER));
    // return agent.getValue(OPERATING_SYSTEM_NAME) + " - " + agent.getValue(AGENT_NAME) + " - " +
    // agent.getValue(DEVICE_NAME);
    return agent.getValue(AGENT_NAME) + " - " + agent.getValue(DEVICE_NAME);
  }
}

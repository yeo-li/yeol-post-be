package com.yeo_li.yeol_post.domain.visitor.command;

import com.yeo_li.yeol_post.domain.visitor.domain.AccessLog;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AccessLogCreateCommand(
    @NotNull UUID visitorId,
    @NotNull String visitorHash,
    String referer,
    String osType,
    String browserType,
    String pageUrl
) {

    public AccessLog toEntity() {
        AccessLog accessLog = new AccessLog();
        accessLog.setVisitorId(visitorId);
        accessLog.setVisitorHash(visitorHash);
        accessLog.setReferer(referer);
        accessLog.setOsType(osType);
        accessLog.setBrowserType(browserType);
        accessLog.setPageUrl(pageUrl);

        return accessLog;
    }
}

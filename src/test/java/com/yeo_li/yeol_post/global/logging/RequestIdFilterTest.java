package com.yeo_li.yeol_post.global.logging;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestIdFilterTest {

    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

    private final RequestIdFilter requestIdFilter = new RequestIdFilter();

    @Test
    void doFilter_유효한_requestId가_있으면_MDC와_응답헤더에_유지한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> requestIdInChain = new AtomicReference<>();

        request.addHeader(StructuredLog.REQUEST_ID_HEADER, "req-123");

        requestIdFilter.doFilter(request, response,
            (servletRequest, servletResponse) ->
                requestIdInChain.set(MDC.get(StructuredLog.REQUEST_ID_MDC_KEY)));

        assertThat(requestIdInChain.get()).isEqualTo("req-123");
        assertThat(response.getHeader(StructuredLog.REQUEST_ID_HEADER)).isEqualTo("req-123");
        assertThat(MDC.get(StructuredLog.REQUEST_ID_MDC_KEY)).isNull();
    }

    @Test
    void doFilter_requestId가_규격에_맞지_않으면_UUID로_재생성한다() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> requestIdInChain = new AtomicReference<>();

        request.addHeader(StructuredLog.REQUEST_ID_HEADER, "bad request id");

        requestIdFilter.doFilter(request, response,
            (servletRequest, servletResponse) ->
                requestIdInChain.set(MDC.get(StructuredLog.REQUEST_ID_MDC_KEY)));

        assertThat(requestIdInChain.get()).matches(UUID_PATTERN);
        assertThat(response.getHeader(StructuredLog.REQUEST_ID_HEADER)).isEqualTo(
            requestIdInChain.get());
        assertThat(MDC.get(StructuredLog.REQUEST_ID_MDC_KEY)).isNull();
    }
}

package com.ai.util;

import com.ai.common.util.SensitiveDataRedactor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SensitiveDataRedactorTest {

    @Test
    void shouldReturnNullForNullInput() {
        assertThat(SensitiveDataRedactor.redact(null)).isNull();
    }

    @Test
    void shouldReturnBlankForBlankInput() {
        assertThat(SensitiveDataRedactor.redact("")).isEqualTo("");
        assertThat(SensitiveDataRedactor.redact("   ")).isEqualTo("   ");
    }

    @Test
    void shouldRedactPasswordFieldWithColon() {
        String input = "{\"password\": \"secret123\", \"name\": \"test\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("secret123");
        assertThat(result).contains("test");
    }

    @Test
    void shouldRedactTokenField() {
        String input = "{\"token\": \"abc123token\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("abc123token");
    }

    @Test
    void shouldRedactSecretField() {
        String input = "secret: mySecretValue";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("mySecretValue");
    }

    @Test
    void shouldRedactApiKeyField() {
        String input = "{\"apikey\": \"sk-123456\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("sk-123456");
    }

    @Test
    void shouldRedactAuthorizationField() {
        String input = "authorization: Bearer token123";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("Bearer token123");
    }

    @Test
    void shouldRedactCookieField() {
        String input = "{\"cookie\": \"session=abc123\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("session=abc123");
    }

    @Test
    void shouldRedactFieldWithEqualsSign() {
        String input = "password=secret123";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("secret123");
    }

    @Test
    void shouldBeCaseInsensitive() {
        String input = "{\"PASSWORD\": \"secret\", \"Token\": \"abc\", \"SECRET\": \"xyz\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).doesNotContain("secret").doesNotContain("abc").doesNotContain("xyz");
    }

    @Test
    void shouldNotRedactNonSensitiveFields() {
        String input = "{\"username\": \"john\", \"email\": \"john@example.com\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("john");
        assertThat(result).contains("john@example.com");
    }

    @Test
    void shouldTruncateLongStrings() {
        String longInput = "a".repeat(15000);

        String result = SensitiveDataRedactor.truncate(longInput);

        assertThat(result.length()).isLessThan(15000);
        assertThat(result).endsWith("... [TRUNCATED]");
    }

    @Test
    void shouldNotTruncateShortStrings() {
        String shortInput = "short string";

        String result = SensitiveDataRedactor.truncate(shortInput);

        assertThat(result).isEqualTo(shortInput);
    }

    @Test
    void shouldReturnNullForTruncateNull() {
        assertThat(SensitiveDataRedactor.truncate(null)).isNull();
    }

    @Test
    void shouldIdentifySensitiveFieldNames() {
        assertThat(SensitiveDataRedactor.isSensitiveField("password")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("token")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("secret")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("apikey")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("authorization")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("cookie")).isTrue();
    }

    @Test
    void shouldIdentifySensitiveFieldNamesCaseInsensitive() {
        assertThat(SensitiveDataRedactor.isSensitiveField("PASSWORD")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("Token")).isTrue();
        assertThat(SensitiveDataRedactor.isSensitiveField("SECRET")).isTrue();
    }

    @Test
    void shouldNotIdentifyNonSensitiveFieldNames() {
        assertThat(SensitiveDataRedactor.isSensitiveField("username")).isFalse();
        assertThat(SensitiveDataRedactor.isSensitiveField("email")).isFalse();
        assertThat(SensitiveDataRedactor.isSensitiveField("name")).isFalse();
    }

    @Test
    void shouldReturnFalseForNullFieldName() {
        assertThat(SensitiveDataRedactor.isSensitiveField(null)).isFalse();
    }

    @Test
    void shouldHandleMultipleSensitiveFieldsInOneString() {
        String input = "{\"password\": \"pass1\", \"token\": \"tok1\", \"secret\": \"sec1\"}";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).doesNotContain("pass1");
        assertThat(result).doesNotContain("tok1");
        assertThat(result).doesNotContain("sec1");
    }

    @Test
    void shouldRedactQuotedFieldNames() {
        String input = "'password': 'secret123'";

        String result = SensitiveDataRedactor.redact(input);

        assertThat(result).contains("[REDACTED]");
        assertThat(result).doesNotContain("secret123");
    }
}

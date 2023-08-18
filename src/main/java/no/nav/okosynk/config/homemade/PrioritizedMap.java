package no.nav.okosynk.config.homemade;

import java.util.Map;

public record PrioritizedMap(Map<String, String> map, int priority) {
}

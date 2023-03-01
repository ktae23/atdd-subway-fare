package nextstep.subway.domain.fare;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DistanceFarePolicy {
    private static final List<DistancePolicy> farePolicies = List.of(
            new FirstPolicy(),
            new BetweenPolicy(10, 50, 5),
            new LastPolicy(50, 8)
    );

    public int getFare(final int distance) {
        return farePolicies.stream()
                .mapToInt(farePolicy -> farePolicy.getFare(distance))
                .sum();
    }
}
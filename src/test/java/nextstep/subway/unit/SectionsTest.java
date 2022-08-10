package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class SectionsTest {

    Station 교대역;
    Station 강남역;
    Station 삼성역;
    Station 남부터미널역;

    Line 이호선;

    Sections sections;

    @BeforeEach
    void setUp() {
        //given
        교대역 = createStation(1L, "교대역");
        강남역 = createStation(2L, "강남역");
        남부터미널역 = createStation(3L, "남부터미널역");
        삼성역 = createStation(4L, "삼성역");

        이호선 = new Line("2호선", "red");

        이호선.addSection(교대역, 강남역, 3, 5);
        이호선.addSection(강남역, 삼성역, 5, 10);
        sections = new Sections(이호선.getSections());
    }

    private Station createStation(long id, String name) {
        Station station = new Station(name);
        ReflectionTestUtils.setField(station, "id", id);

        return station;
    }

    @Test
    @DisplayName("총 소요 시간")
    void totalDuration() {
        int totalDuration = sections.totalDuration();

        assertThat(totalDuration).isEqualTo(15);
    }

    @Test
    @DisplayName("구간 가운데에 추가")
    void addSection() {
        sections.add(new Section(이호선, 강남역, 남부터미널역, 2, 5));

        Section section = sections.getSections().get(sections.getSections().size() - 2);

        assertThat(section.getUpStation()).isEqualTo(남부터미널역);
        assertThat(section.getDistance()).isEqualTo(3);
        assertThat(section.getDuration()).isEqualTo(5);
    }

    @Test
    @DisplayName("가운데 구간 삭제")
    void deleteSection() {
        //when
        sections.delete(강남역);

        //then
        List<Section> sectionList = sections.getSections();

        Section section = sectionList.get(0);
        assertThat(section.getDuration()).isEqualTo(15);
        assertThat(section.getDistance()).isEqualTo(8);
    }

}
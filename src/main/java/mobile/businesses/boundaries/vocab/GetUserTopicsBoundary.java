package mobile.businesses.boundaries.vocab;

import java.util.List;

public interface GetUserTopicsBoundary {
    List<String> execute(String userId);
}

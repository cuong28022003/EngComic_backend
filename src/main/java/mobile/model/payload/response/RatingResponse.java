package mobile.model.payload.response;

public class RatingResponse {
    private int userRating;
    private double averageRating;
    private int totalReviews;

    public RatingResponse(int userRating, double averageRating, int totalReviews) {
        this.userRating = userRating;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    // Getters
    public int getUserRating() {
        return userRating;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }
}

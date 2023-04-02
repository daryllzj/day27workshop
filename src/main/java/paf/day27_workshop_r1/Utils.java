package paf.day27_workshop_r1;

import java.time.LocalDate;

import org.bson.Document;
import org.springframework.util.MultiValueMap;

import paf.day27_workshop_r1.exceptions.ReviewException;
import paf.day27_workshop_r1.model.Review;

public class Utils {

    public static Review toReview(MultiValueMap<String, String> form) {

        String name = form.getFirst("name");
        String comment = form.getFirst("comment");
        

        try {
            String ratingInString = form.getFirst("rating");
            Integer rating = Integer.parseInt(ratingInString);
            if (rating <1 || rating >10) {
                throw new ReviewException("rating must be within 1 and 10");
            }

            try {
                String gidInString = form.getFirst("game_id");
                Integer game_id = Integer.parseInt(gidInString);

                Review review = new Review(name, rating, comment, game_id);
                return review;
            } catch (Exception e) {
                throw new ReviewException("game id must be integer");
            }
            
        } catch (Exception e) {
            throw e;
        }
        
    }

    public static Document toDocument(Review review, String gameName) {

        Document document = new Document();
        document.append("user", review.getName());
        document.append("rating", review.getRating());
        document.append("comment", review.getComment());
        document.append("ID", review.getGame_id());
        document.append("posted", LocalDate.now().toString());
        document.append("name", gameName);

        return document;
        
    }
    
}

package paf.day27_workshop_r1.repo;

import java.time.LocalDate;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import lombok.extern.slf4j.Slf4j;
import paf.day27_workshop_r1.Utils;
import paf.day27_workshop_r1.exceptions.ReviewException;
import paf.day27_workshop_r1.model.Review;

@Slf4j
@Repository
public class ReviewRepo {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String C_GAMES = "game";

    private static final String C_COMMENTS = "comment";

    public Document saveReview(Review review) {

        String gameName = checkIfGameExisitsAndRetrievesGameName(review);

        Document finalComment = Utils.toDocument(review, gameName);

        log.info("comment to be saved>>> " + finalComment.toString());

        Document newDoc = mongoTemplate.insert(finalComment,C_COMMENTS);

        ObjectId id = newDoc.getObjectId("_id");

        log.info("_id>>> " + id.toString());
        log.info("newDoc>>> " + newDoc.toString());
       
        return newDoc;
    }

    public String checkIfGameExisitsAndRetrievesGameName(Review review) {
        
        try {

            Integer gid = review.getGame_id();

            Criteria criteria = Criteria.where("gid").is(gid);

            Query query = Query.query(criteria);

            List<Document> listOfComments = mongoTemplate.find(query, Document.class, C_COMMENTS);

            Document comment = listOfComments.get(0);

            if (null == comment) {
                throw new ReviewException("game does not exists");
            } 

            List<Document> listOfGames = mongoTemplate.find(query, Document.class, C_GAMES);

            Document game = listOfGames.get(0);

            log.info(game.toString());

            game.getString("name");
            
            return game.getString("name");
            
        } catch (Exception e) {
            throw new ReviewException("invalid id format");
        }

    }
    
    public JsonObject updateReview(String id, Review review) {

        try {
            ObjectId docId = new ObjectId(id);
            Document existingCommment = mongoTemplate.findById(docId, Document.class, C_COMMENTS);

            if (null== existingCommment) {
                throw new ReviewException("comment does not exist");
            }

            JsonObject comment = Json.createObjectBuilder()
            .add("comment", review.getComment())
            .add("rating", review.getRating())
            .add("posted", LocalDate.now().toString())
            .build();

            log.info("commentinJson>>> " + comment.toString());

            Document commentInDoc = Document.parse(comment.toString());

            log.info("commentinDoc>>> " + commentInDoc.toString());

            Criteria criteria = Criteria.where("_id").is(id);

            Query query = Query.query(criteria);

            Update updateOps = new Update()
            .set("comment", review.getComment())
            .set("rating", review.getRating())
            .push("edited", commentInDoc);

            UpdateResult updateResult = mongoTemplate.upsert(query, updateOps, getClass(), C_COMMENTS);

            log.info("updateResult>>> " + updateResult.toString());

            return comment;


        } catch (IllegalArgumentException e) {
            throw new ReviewException("invalid comment id format");
        } catch (Exception e) {
            throw e;
        }
        
    }

    public JsonObject getReview(String id) {

        try {
            ObjectId docId = new ObjectId(id);
            Document existingCommment = mongoTemplate.findById(docId, Document.class, C_COMMENTS);

            if (null== existingCommment) {
                throw new ReviewException("comment does not exist");
            }

            List<Document> comments = existingCommment.getList("edited", Document.class);

            Boolean bEdited = false;

            if (comments.size()>0) {
                bEdited = true;
            }

            log.info("commentsInList" + comments.toString());

            JsonObject comment = Json.createObjectBuilder()
            .add("user", existingCommment.getString("user"))
            .add("rating", existingCommment.getInteger("rating"))
            .add("comment", existingCommment.getString("comment"))
            .add("ID", existingCommment.getInteger("ID"))
            .add("posted", existingCommment.getString("posted"))
            .add("name", existingCommment.getString("name"))
            .add("edited", bEdited)
            .add("timestamp", LocalDate.now().toString())
            .build();


            log.info("commentinJson>>> " + comment.toString());
    
            return comment;

        } catch (IllegalArgumentException e) {
            throw new ReviewException("invalid comment id format");
        } catch (Exception e) {
            throw e;
        }
        
    }

    public JsonObject getReviewHistory(String id) {

        try {
            ObjectId docId = new ObjectId(id);
            Document existingCommment = mongoTemplate.findById(docId, Document.class, C_COMMENTS);

            if (null== existingCommment) {
                throw new ReviewException("comment does not exist");
            }

            List<Document> comments = existingCommment.getList("edited", Document.class);

            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

            for (Document document : comments) {
                JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
                jsonObjectBuilder.add("comment", document.getString("comment"));
                jsonObjectBuilder.add("rating", document.getInteger("rating"));
                jsonObjectBuilder.add("posted", document.getString("posted"));
                jsonArrayBuilder.add(jsonObjectBuilder);
            }

            // JsonArray jsonArray = jsonArrayBuilder.build();

            // log.info("commentsInJsonArray" + jsonArray.toString());

            JsonObject comment = Json.createObjectBuilder()
            .add("user", existingCommment.getString("user"))
            .add("rating", existingCommment.getInteger("rating"))
            .add("comment", existingCommment.getString("comment"))
            .add("ID", existingCommment.getInteger("ID"))
            .add("posted", existingCommment.getString("posted"))
            .add("name", existingCommment.getString("name"))
            .add("edited", jsonArrayBuilder)
            .add("timestamp", LocalDate.now().toString())
            .build();

            // log.info("commentinJson>>> " + comment.toString());
    
            return comment;

        } catch (IllegalArgumentException e) {
            throw new ReviewException("invalid comment id format");
        } catch (Exception e) {
            throw e;
        }
        
    }
    
}

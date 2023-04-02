package paf.day27_workshop_r1.controller;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import paf.day27_workshop_r1.Utils;
import paf.day27_workshop_r1.model.Review;
import paf.day27_workshop_r1.repo.ReviewRepo;

@Slf4j
@RestController
@RequestMapping
public class ReviewRestController {

    @Autowired
    ReviewRepo reviewRepo;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> submitReview(@RequestBody MultiValueMap<String, String> form ) {
        
        try {
            Review review = Utils.toReview(form);

            Document savedComment = reviewRepo.saveReview(review);

            log.info("comment value>>> " + savedComment.getString("comment"));

            JsonObject response = Json.createObjectBuilder()
            .add("user", savedComment.getString("user"))
            .add("rating", savedComment.getInteger("rating"))
            .add("comment", savedComment.getString("comment"))
            .add("ID", savedComment.getInteger("ID"))
            .add("posted", savedComment.getString("posted"))
            .add("name", savedComment.getString("name"))
            .build();

            return ResponseEntity.ok(response.toString());
            
        } catch (Exception e) {
                JsonObject err = Json.createObjectBuilder()
				.add("message", e.getMessage())
				.build();
			return ResponseEntity.status(400).body(err.toString());
        }
        
    }

    @PostMapping(path = "/review/{review_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> submitReview(@PathVariable("review_id") String id , @RequestBody Review review ) {

        
        try {
            JsonObject comment = reviewRepo.updateReview(id, review);

            return ResponseEntity.ok(comment.toString());
            
        } catch (Exception e) {
                JsonObject err = Json.createObjectBuilder()
				.add("message", e.getMessage())
				.build();
			return ResponseEntity.status(400).body(err.toString());
        }
        
    }


    @GetMapping (path = "/review/{review_id}/history")
    public ResponseEntity<String> getReview(@PathVariable("review_id") String id) {

        try {
            JsonObject comment = reviewRepo.getReviewHistory(id);

            return ResponseEntity.ok(comment.toString());
            
        } catch (Exception e) {
                JsonObject err = Json.createObjectBuilder()
				.add("message", e.getMessage())
				.build();
			return ResponseEntity.status(400).body(err.toString());
        }
        
    }

}

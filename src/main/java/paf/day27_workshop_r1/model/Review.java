package paf.day27_workshop_r1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private String name;

    private Integer rating;

    private String comment;

    private Integer game_id;
    
}

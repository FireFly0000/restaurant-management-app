package com.restaurant.userservice.model.embedded;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ReviewId implements Serializable {
    private UUID userId;
    private UUID bussinessId;

    public ReviewId(){}
    public ReviewId(UUID userId, UUID bussinessId){
        this.userId = userId;
        this.bussinessId = bussinessId;
    }

    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(!(o instanceof  ReviewId that)) return false;
        return Objects.equals(userId, that.userId)
               && Objects.equals(bussinessId, that.bussinessId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(userId, bussinessId);
    }
}

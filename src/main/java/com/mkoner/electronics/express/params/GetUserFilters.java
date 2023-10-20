package com.mkoner.electronics.express.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetUserFilters {

    private Long userId;
    private String name;
    private String email;
    private String number;

    @Override
    public String toString(){
        return "CreateUserParams [userId=" + userId +", name=" + name + ", email=" + email +
                ", number=" + number + "]";
    }
}

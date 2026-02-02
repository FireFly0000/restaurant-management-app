package com.restaurant.businessservice.model.embedded;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class RolePermissionId {
    private UUID roleId;
    private UUID permissionId;

    public RolePermissionId(){}
    public RolePermissionId(UUID roleId, UUID permissionId){
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    @Override
    public boolean equals(Object o){
        if(this == o ) return true;
        if(!(o instanceof  RolePermissionId that)) return false;
        return Objects.equals(roleId, that.roleId)
               && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(roleId, permissionId);
    }
}

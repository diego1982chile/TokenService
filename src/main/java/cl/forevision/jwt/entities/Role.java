package cl.forevision.jwt.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by root on 09-12-22.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Role extends AbstractPersistableEntity<String> {

    @Id
    String rolename;

    @Override
    public String getId() {
        return rolename;
    }

    @Override
    public String toString() {
        return rolename;
    }
}

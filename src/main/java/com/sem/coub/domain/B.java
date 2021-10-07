package com.sem.coub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A B.
 */
@Entity
@Table(name = "b")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class B implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Column(name = "jhi_group")
    private String group;

    @Column(name = "start_sate")
    private Instant startSate;

    @ManyToOne
    @JsonIgnoreProperties(value = { "groupIds" }, allowSetters = true)
    private A a;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public B id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGroup() {
        return this.group;
    }

    public B group(String group) {
        this.setGroup(group);
        return this;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Instant getStartSate() {
        return this.startSate;
    }

    public B startSate(Instant startSate) {
        this.setStartSate(startSate);
        return this;
    }

    public void setStartSate(Instant startSate) {
        this.startSate = startSate;
    }

    public A getA() {
        return this.a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B a(A a) {
        this.setA(a);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof B)) {
            return false;
        }
        return id != null && id.equals(((B) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "B{" +
            "id=" + getId() +
            ", group='" + getGroup() + "'" +
            ", startSate='" + getStartSate() + "'" +
            "}";
    }
}

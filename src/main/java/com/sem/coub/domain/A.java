package com.sem.coub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * A A.
 */
@Entity
@Table(name = "a")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class A implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @Column(name = "id", length = 36, nullable = false, unique = true)
    private UUID id;

    @Type(type = "uuid-char")
    @Column(name = "group_id", length = 36)
    private UUID groupId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "hire_date")
    private Instant hireDate;

    @OneToMany(mappedBy = "a")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "a" }, allowSetters = true)
    private Set<B> groupIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public A id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGroupId() {
        return this.groupId;
    }

    public A groupId(UUID groupId) {
        this.setGroupId(groupId);
        return this;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public A firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public A lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public A email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public A phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Instant getHireDate() {
        return this.hireDate;
    }

    public A hireDate(Instant hireDate) {
        this.setHireDate(hireDate);
        return this;
    }

    public void setHireDate(Instant hireDate) {
        this.hireDate = hireDate;
    }

    public Set<B> getGroupIds() {
        return this.groupIds;
    }

    public void setGroupIds(Set<B> bs) {
        if (this.groupIds != null) {
            this.groupIds.forEach(i -> i.setA(null));
        }
        if (bs != null) {
            bs.forEach(i -> i.setA(this));
        }
        this.groupIds = bs;
    }

    public A groupIds(Set<B> bs) {
        this.setGroupIds(bs);
        return this;
    }

    public A addGroupId(B b) {
        this.groupIds.add(b);
        b.setA(this);
        return this;
    }

    public A removeGroupId(B b) {
        this.groupIds.remove(b);
        b.setA(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof A)) {
            return false;
        }
        return id != null && id.equals(((A) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "A{" +
            "id=" + getId() +
            ", groupId='" + getGroupId() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", hireDate='" + getHireDate() + "'" +
            "}";
    }
}

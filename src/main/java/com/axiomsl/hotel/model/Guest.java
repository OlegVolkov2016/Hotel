package com.axiomsl.hotel.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * Guest Entity
 */
@Entity
@Table(name = "guests")
public class Guest {
    private Long id;
    private int version;
    private String login;
    private String password;
    private String confirm;
    private GuestRole role;
    private String firstName;
    private String lastName;
    private Set<Reservation> reservations = new HashSet<Reservation>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Version
    @Column(name = "guest_version")
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @NotEmpty(message="{not_empty_text}")
    @Column(name = "guest_login")
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @NotEmpty(message="{not_empty_text}")
    @Column(name = "guest_password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        checkPassword();
    }

    @NotEmpty(message="{not_match_text}")
    @Column(name = "guest_confirm")
    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    private void checkPassword() {
        if (this.password == null || this.confirm == null) {
            return;
        } else if (!this.password.equals(this.confirm)) {
            this.confirm = null;
        }
    }

    @Column(name = "guest_role")
    @Enumerated(EnumType.STRING)
    public GuestRole getRole() {
        return role;
    }

    public void setRole(GuestRole role) {
        this.role = role;
    }

    @NotEmpty(message="{not_empty_text}")
    @Column(name = "guest_firstname")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @NotEmpty(message="{not_empty_text}")
    @Column(name = "guest_lastname")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @OneToMany(mappedBy = "guest",
        cascade = CascadeType.ALL)
    @OrderBy("from ASC, to ASC")
    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", version=" + version +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", confirm='" + confirm + '\'' +
                ", role=" + role +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}

//package ru.yermolenko.model;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.checkerframework.common.aliasing.qual.Unique;
//
//import javax.persistence.*;
//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.util.HashSet;
//import java.util.Set;
//
//@Data
//@NoArgsConstructor
//@Builder
//@AllArgsConstructor
//@Entity
//@Table(	name = "app_users",
//		uniqueConstraints = {
//				@UniqueConstraint(columnNames = "username"),
//				@UniqueConstraint(columnNames = "email")
//		})
//public class AppUser {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//
//	@NotBlank
//	@Size(max = 20)
//	private String username;
//
//	@NotBlank
//	@Size(max = 50)
//	@Email
//	@Unique
//	private String email;
//
//	@NotBlank
//	@Size(max = 120)
//	private String password;
//
//	@ManyToMany(fetch = FetchType.LAZY)
//	@JoinTable(	name = "user_role",
//				joinColumns = @JoinColumn(name = "user_id"),
//				inverseJoinColumns = @JoinColumn(name = "role_id"))
//	private Set<Role> roles = new HashSet<>();
//	private Boolean isActive;
//
//	public AppUser(String username, String email, String password) {
//		this.username = username;
//		this.email = email;
//		this.password = password;
//	}
//}

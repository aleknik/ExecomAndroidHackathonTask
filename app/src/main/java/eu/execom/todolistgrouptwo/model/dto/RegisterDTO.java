package eu.execom.todolistgrouptwo.model.dto;


public class RegisterDTO {

    private final String email;

    private final String password;

    private final String confirmPassword;

    public RegisterDTO(String email, String password, String confirmPassword) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public String toString() {
        return "RegisterDTO{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}

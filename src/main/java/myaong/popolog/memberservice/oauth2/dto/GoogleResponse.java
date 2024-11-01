package myaong.popolog.memberservice.oauth2.dto;

import java.util.Map;

public class GoogleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private final String providerId;

    public GoogleResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.providerId = this.getProvider() + "_" + attribute.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return nullCase("name");
    }

    @Override
    public String getProfilePicUrl() {
        return nullCase("profile_image");
    }

    @Override
    public String getNickname() {
        return ((this.attribute.get("nickname").toString()) == null) ?
                this.attribute.get("name").toString() : this.attribute.get("nickname").toString();
    }

    @Override
    public String getPermission() {
        return "";
    }

    public String nullCase(String key) {
        return ((this.attribute.get(key).toString()) == null) ?
                null : this.attribute.get(key).toString();
    }
}
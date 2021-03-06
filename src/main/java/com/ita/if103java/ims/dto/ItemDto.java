package com.ita.if103java.ims.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;


public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String unit;
    @NotBlank
    private String description;
    @NotNull
    private int volume;
    private Long accountId;
    private boolean active;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return volume == itemDto.volume &&
            active == itemDto.active &&
            Objects.equals(id, itemDto.id) &&
            Objects.equals(name, itemDto.name) &&
            Objects.equals(unit, itemDto.unit) &&
            Objects.equals(description, itemDto.description) &&
            Objects.equals(accountId, itemDto.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unit, description, volume, accountId, active);
    }

    @Override
    public String toString() {
        return "ItemDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", unit='" + unit + '\'' +
            ", description='" + description + '\'' +
            ", volume=" + volume +
            ", accountId=" + accountId +
            ", active=" + active +
            '}';
    }
}

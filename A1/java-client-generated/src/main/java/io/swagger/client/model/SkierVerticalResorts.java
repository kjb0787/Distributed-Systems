/*
 * Ski Data API for NEU Seattle distributed systems course
 * An API for an emulation of skier managment system for RFID tagged lift tickets. Basis for CS6650 Assignments for 2019
 *
 * OpenAPI spec version: 1.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
/**
 * SkierVerticalResorts
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2021-09-29T01:13:01.599Z[GMT]")
public class SkierVerticalResorts {
  @SerializedName("seasonID")
  private String seasonID = null;

  @SerializedName("totalVert")
  private Integer totalVert = null;

  public SkierVerticalResorts seasonID(String seasonID) {
    this.seasonID = seasonID;
    return this;
  }

   /**
   * Get seasonID
   * @return seasonID
  **/
  @Schema(description = "")
  public String getSeasonID() {
    return seasonID;
  }

  public void setSeasonID(String seasonID) {
    this.seasonID = seasonID;
  }

  public SkierVerticalResorts totalVert(Integer totalVert) {
    this.totalVert = totalVert;
    return this;
  }

   /**
   * Get totalVert
   * @return totalVert
  **/
  @Schema(description = "")
  public Integer getTotalVert() {
    return totalVert;
  }

  public void setTotalVert(Integer totalVert) {
    this.totalVert = totalVert;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SkierVerticalResorts skierVerticalResorts = (SkierVerticalResorts) o;
    return Objects.equals(this.seasonID, skierVerticalResorts.seasonID) &&
        Objects.equals(this.totalVert, skierVerticalResorts.totalVert);
  }

  @Override
  public int hashCode() {
    return Objects.hash(seasonID, totalVert);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SkierVerticalResorts {\n");
    
    sb.append("    seasonID: ").append(toIndentedString(seasonID)).append("\n");
    sb.append("    totalVert: ").append(toIndentedString(totalVert)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

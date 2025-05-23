package entity;


public class Category {
  private Integer categoryId;
  private String categoryName;

  public Integer getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Integer categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public String toString() {
    return "ID=" + categoryId + ", categoryName=" + categoryName;
  }
}

package jpabook.jpashop.service;

import jpabook.jpashop.controller.BookForm;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateItemDto {
      private int price;
      private String name;
      private int stockQuantity;

      public UpdateItemDto(BookForm form) {
            this.price = form.getPrice();
            this.name = form.getName();
            this.stockQuantity = form.getStockQuantity();
      }
}

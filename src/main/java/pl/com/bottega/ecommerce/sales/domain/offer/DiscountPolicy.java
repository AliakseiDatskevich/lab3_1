package pl.com.bottega.ecommerce.sales.domain.offer;

import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public interface DiscountPolicy {

    public Discount applyDiscount(Product product, int quantity, Money regularCost);
}

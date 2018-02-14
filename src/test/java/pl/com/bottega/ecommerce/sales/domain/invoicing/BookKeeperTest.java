package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

public class BookKeeperTest {

    @Test
    public void issuanceRequestForInvoiceWithOnePosition() {
        Money money = new Money(12, "USD");
        Id id = new Id("12");
        String productName = "Onion";
        String description = "VEGE";
        int expectedSize = 1;

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = Mockito.mock(InvoiceRequest.class);
        ProductData productData = new ProductData(id, money, productName, ProductType.FOOD, new Date());
        List<RequestItem> requestItems = new ArrayList<>();
        RequestItem requestItem = new RequestItem(productData, 2, money);
        requestItems.add(requestItem);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);

        Mockito.when(invoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoice.getItems().size(), is(expectedSize));

    }

    @Test
    public void issuanceRequestForInvoiceWithTwoPositionCallCalculateTaxTwice() {
        Money money = new Money(12, "USD");
        Id id = new Id("12");
        String productName = "Onion";
        String description = "VEGE";

        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        InvoiceRequest invoiceRequest = Mockito.mock(InvoiceRequest.class);
        ProductData productData = new ProductData(id, money, productName, ProductType.FOOD, new Date());
        List<RequestItem> requestItems = new ArrayList<>();
        RequestItem requestItem = new RequestItem(productData, 2, money);
        requestItems.add(requestItem);
        requestItems.add(requestItem);
        TaxPolicy taxPolicy = Mockito.mock(TaxPolicy.class);

        Mockito.when(invoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

}

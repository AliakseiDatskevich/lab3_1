package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
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

    private Money money;
    private String description;
    private BookKeeper bookKeeper;
    private InvoiceRequest mockedInvoiceRequest;
    private TaxPolicy mockedTaxPolicy;
    private RequestItem requestItem;
    private List<RequestItem> requestItems;
    private int quantity;

    @Before
    public void initialize() {
        money = new Money(12, "USD");
        Id id = new Id("12");
        String productName = "Onion";
        description = "VEGE";
        quantity = 2;

        bookKeeper = new BookKeeper(new InvoiceFactory());
        mockedInvoiceRequest = Mockito.mock(InvoiceRequest.class);
        ProductData productData = new ProductData(id, money, productName, ProductType.FOOD, new Date());
        requestItem = new RequestItem(productData, quantity, money);
        mockedTaxPolicy = Mockito.mock(TaxPolicy.class);
        requestItems = new ArrayList<>();
    }

    @Test
    public void issuanceRequestForInvoiceWithOnePosition() {
        int expectedSize = 1;
        requestItems.add(requestItem);

        Mockito.when(mockedInvoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(mockedTaxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        Invoice invoice = bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

        assertThat(invoice.getItems().size(), is(expectedSize));

    }

    @Test
    public void issuanceRequestForInvoiceWithTwoPositionCallCalculateTaxTwice() {
        int expectedInvocations = 2;
        requestItems.add(requestItem);
        requestItems.add(requestItem);

        Mockito.when(mockedInvoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(mockedTaxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

        Mockito.verify(mockedTaxPolicy, Mockito.times(expectedInvocations))
                .calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void issuanceRequestForInvoiceCheckQuantityOfFirstPosition() {
        requestItems.add(requestItem);

        Mockito.when(mockedInvoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(mockedTaxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        Invoice invoice = bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

        assertThat(invoice.getItems().get(0).getQuantity(), is(quantity));
    }

    @Test
    public void issuanceRequestForInvoiceWithZeroPositionNoCallCalculateTax() {
        int expectedInvocations = 0;

        Mockito.when(mockedInvoiceRequest.getItems()).thenReturn(requestItems);
        Mockito.when(mockedTaxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));

        bookKeeper.issuance(mockedInvoiceRequest, mockedTaxPolicy);

        Mockito.verify(mockedTaxPolicy, Mockito.times(expectedInvocations))
                .calculateTax(any(ProductType.class), any(Money.class));
    }

}

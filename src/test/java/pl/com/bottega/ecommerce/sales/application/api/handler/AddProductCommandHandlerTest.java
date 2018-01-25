package pl.com.bottega.ecommerce.sales.application.api.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class AddProductCommandHandlerTest {

	private ReservationRepository mockedReservationRepository;
	private ProductRepository mockedProductRepository;
	private Reservation reservation;
	private Reservation spy;
	private Product product;
	private AddProductCommandHandler handler;
	private AddProductCommand productCommand;

	@Before
	public void setUp() {
		handler = new AddProductCommandHandler();
		productCommand = new AddProductCommand(new Id("1"), new Id("2"), 3);
		mockedReservationRepository = mock(ReservationRepository.class);
		mockedProductRepository = mock(ProductRepository.class);
		reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
				new ClientData(Id.generate(), "Client"), new Date());
		product = new Product(Id.generate(), new Money(10), "Product", ProductType.STANDARD);
		spy = spy(reservation);
		Whitebox.setInternalState(handler, "reservationRepository", mockedReservationRepository);
		Whitebox.setInternalState(handler, "productRepository", mockedProductRepository);
	}

	@Test
	public void addMethodShouldBeCalledOnce() {
		when(mockedProductRepository.load(any(Id.class))).thenReturn(product);
		when(mockedReservationRepository.load(any(Id.class))).thenReturn(spy);

		handler.handle(productCommand);

		verify(spy, times(1)).add(product, 3);
	}

	@Test
	public void saveMethodShouldBeCalledOnce() {
		when(mockedProductRepository.load(any(Id.class))).thenReturn(product);
		when(mockedReservationRepository.load(any(Id.class))).thenReturn(spy);

		handler.handle(productCommand);

		verify(mockedReservationRepository, times(1)).save(any(Reservation.class));
	}
}

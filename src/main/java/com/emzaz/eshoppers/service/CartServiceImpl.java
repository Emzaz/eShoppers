package com.emzaz.eshoppers.service;

import com.emzaz.eshoppers.dtos.Cart;
import com.emzaz.eshoppers.dtos.CartItem;
import com.emzaz.eshoppers.dtos.Product;
import com.emzaz.eshoppers.dtos.UserDto;
import com.emzaz.eshoppers.exception.CartItemNotFoundException;
import com.emzaz.eshoppers.exception.ProductNotFoundException;
import com.emzaz.eshoppers.repository.CartItemRepository;
import com.emzaz.eshoppers.repository.CartRepository;
import com.emzaz.eshoppers.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class CartServiceImpl implements CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           ProductRepository productRepository,
                           CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public Cart getCartByUser(UserDto currentUser) {
        Optional<Cart> optionalCart = cartRepository.findByUser(currentUser);

        return optionalCart.orElseGet(() -> createNewCart(currentUser));
    }

    private Cart createNewCart(UserDto currentUser) {
        Cart cart = new Cart();
        cart.setUserDto(currentUser);

        return cartRepository.save(cart);
    }

    @Override
    public void  addProductToCart(String productId, Cart cart) throws ProductNotFoundException {
        Product product = findProduct(productId);

        addProductToCart(product, cart);

        updateCart(cart);
    }

    @Override
    public void subProductToCart(String productId, Cart cart) {
        Product product = findProduct(productId);

        subProductToCart(product, cart);

        updateCart(cart);
    }

    @Override
    public void removeProductToCart(String productId, Cart cart) {
        Product product = findProduct(productId);

        removeProductToCart(product, cart);

        updateCart(cart);
    }

    private Product findProduct(String productId) throws ProductNotFoundException {
        if(productId == null || productId.length() == 0) {
            throw new IllegalArgumentException("Product id can not be null");
        }

        Long id = parseProductId(productId);

        Optional<Product> product = productRepository.findById(id);

        if(!product.isPresent()) {
            throw new ProductNotFoundException("Product Not Found By ID: " +id);
        }

        return product.get();
    }

    private void updateCart(Cart cart) {
        Integer totalItem = getTotalItem(cart);
        BigDecimal totalPrice = calculateTotalPrice(cart);

        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);

        cartRepository.save(cart);
    }

    private void addProductToCart(Product product, Cart cart) {
        Optional<CartItem> cartItemOptional = findSimilarProductInCart(cart, product);

        CartItem cartItem;

        if(cartItemOptional.isPresent()) {
            cartItem = increaseQuantityByOne(cartItemOptional.get());
        } else {
            cartItem = createNewShoppingCartItem(product, cart);
        }

        cart.getCartItems().add(cartItem);
    }

    private Optional<CartItem> find(Set<CartItem> cartItems, Product productToRemove) {
        for (CartItem cartItem : cartItems) {
            if(cartItem.getProduct().equals(productToRemove)) {
                return Optional.of(cartItem);
            }
        }
        return Optional.empty();
    }

    private void subProductToCart(Product productToRemove, Cart cart) throws CartItemNotFoundException {
        Set<CartItem> cartItems = cart.getCartItems();
        Optional<CartItem> itemOptional = find(cartItems, productToRemove);

        CartItem cartItem;
        if(itemOptional.isPresent()) {
            cartItem = itemOptional.get();
        } else {
            throw new CartItemNotFoundException("Cart not found by product: " +productToRemove);
        }

        if(cartItem.getQuantity() > 1) {
            cartItem.setQuantity(cartItem.getQuantity() - 1);

            cartItem.setPrice(cartItem.getPrice().subtract(productToRemove.getPrice()));

            cart.getCartItems().add(cartItem);

            cartItemRepository.update(cartItem);
        } else {
            cart.getCartItems().remove(cartItem);

            cartItemRepository.remove(cartItem);
        }
    }

    private void removeProductToCart(Product productToRemove, Cart cart) throws CartItemNotFoundException {
        Set<CartItem> cartItems = cart.getCartItems();
        Optional<CartItem> itemOptional = find(cartItems, productToRemove);

        CartItem cartItem;
        if(itemOptional.isPresent()) {
            cartItem = itemOptional.get();
        } else {
            throw new CartItemNotFoundException("Cart not found by product : " +productToRemove);
        }

        cart.getCartItems().remove(cartItem);
        cartItemRepository.remove(cartItem);
    }

    private BigDecimal calculateTotalPrice(Cart cart) {
        Set<CartItem> cartItems = cart.getCartItems();
        BigDecimal sum = BigDecimal.ZERO;

        for(CartItem cartItem : cartItems) {
            sum = sum.add(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        return sum;
    }

    private Integer getTotalItem(Cart cart) {
        Set<CartItem> cartItems = cart.getCartItems();
        Integer sum = 0;

        for(CartItem cartItem : cartItems) {
            sum += cartItem.getQuantity();
        }

        return sum;
    }

    private Long parseProductId(String productId) {
        try {
            return Long.parseLong(productId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Product ID must be a number", ex);
        }
    }

    private CartItem createNewShoppingCartItem(Product product, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        cartItem.setPrice(product.getPrice());
        cartItem.setCart(cart);

        return cartItemRepository.save(cartItem);
    }

    private CartItem increaseQuantityByOne(CartItem cartItem) {
        cartItem.setQuantity(cartItem.getQuantity() + 1);

        Product product = cartItem.getProduct();
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        cartItem.setPrice(totalPrice);

        return cartItemRepository.update(cartItem);
    }

    private Optional<CartItem> findSimilarProductInCart(Cart cart, Product product) {
        Set<CartItem> cartItems = cart.getCartItems();
        for(CartItem cartItem : cartItems) {
            if(cartItem.getProduct().getId().equals(product.getId())) {
                return Optional.of(cartItem);
            }
        }
        return Optional.empty();
    }
}

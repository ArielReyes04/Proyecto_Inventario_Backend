package ec.edu.espe.master.service;

import ec.edu.espe.master.dto.response.CategoryResponse;
import ec.edu.espe.master.repository.CategoryRepository;
import ec.edu.espe.master.services.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());
        List<CategoryResponse> res = categoryService.getAllCategories();
        assertNotNull(res);
    }
}

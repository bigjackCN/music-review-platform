package io.github.bigjackcn.musicreview.service;

import io.github.bigjackcn.musicreview.entity.Album;
import io.github.bigjackcn.musicreview.mapper.AlbumMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void getAllAlbums_returnsListFromMapper() {
        Album album = new Album();
        album.setId(1L);
        album.setTitle("OK Computer");
        when(albumMapper.findAll()).thenReturn(List.of(album));

        List<Album> result = albumService.getAllAlbums();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("OK Computer");
    }

    @Test
    void getAlbumById_whenExists_returnsAlbum() {
        Album album = new Album();
        album.setId(1L);
        when(albumMapper.findById(1L)).thenReturn(album);

        Album result = albumService.getAlbumById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAlbumById_whenNotExists_throwsException() {
        when(albumMapper.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> albumService.getAlbumById(99L))
                .isInstanceOf(AlbumNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createAlbum_callsMapperInsert() {
        Album album = new Album();
        album.setTitle("New Album");

        Album result = albumService.createAlbum(album);

        verify(albumMapper, times(1)).insert(album);
        assertThat(result.getTitle()).isEqualTo("New Album");
    }

    @Test
    void deleteAlbum_whenNotExists_throwsException() {
        when(albumMapper.deleteById(99L)).thenReturn(0);

        assertThatThrownBy(() -> albumService.deleteAlbum(99L))
                .isInstanceOf(AlbumNotFoundException.class);
    }
}

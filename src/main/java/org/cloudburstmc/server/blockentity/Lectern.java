package org.cloudburstmc.server.blockentity;

import org.cloudburstmc.server.item.behavior.Item;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public interface Lectern extends BlockEntity {

    boolean hasBook();

    @Nullable
    Item getBook();

    void setBook(@Nullable Item book);

    int getPage();

    void setPage(@Nonnegative int page);

    int getTotalPages();
}

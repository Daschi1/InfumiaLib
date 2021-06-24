package tr.com.infumia.infumialib.paper.element.types;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tr.com.infumia.infumialib.paper.element.PlaceType;
import tr.com.infumia.infumialib.paper.smartinventory.Icon;
import tr.com.infumia.infumialib.paper.smartinventory.InventoryContents;
import tr.com.infumia.infumialib.transformer.TransformedData;
import tr.com.infumia.infumialib.transformer.declarations.GenericDeclaration;

public final class PtNone implements PlaceType {

  public static final PtNone INSTANCE = new PtNone();

  @NotNull
  @Override
  public Serializer getSerializer() {
    return Serializer.INSTANCE;
  }

  @NotNull
  @Override
  public String getType() {
    return "none";
  }

  @Override
  public void place(@NotNull final Icon icon, @NotNull final InventoryContents contents) {
  }

  @Override
  public void serialize(@NotNull final TransformedData transformedData) {
    this.getSerializer().serialize(this, transformedData);
  }

  public static final class Serializer extends PlaceType.Serializer<PtNone> {

    public static final Serializer INSTANCE = new Serializer();

    @NotNull
    @Override
    public Optional<PtNone> deserialize(@NotNull final TransformedData transformedData,
                                        @Nullable final GenericDeclaration declaration) {
      return Optional.of(PtNone.INSTANCE);
    }

    @Override
    public void serialize(@NotNull final PtNone placeType, @NotNull final TransformedData transformedData) {
      super.serialize(placeType, transformedData);
    }
  }
}

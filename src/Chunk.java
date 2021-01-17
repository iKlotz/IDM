class Chunk {
    private byte[] data;
    private long offset;
    private int sizeInBytes;
    private Range range;

    Chunk(byte[] data, long offset, int sizeInBytes, Range range) {
        this.data = data != null ? data.clone() : null;
        this.offset = offset;
        this.sizeInBytes = sizeInBytes;
        this.range = range;
    }

    byte[] getData() {
        return data; }

    long getOffset() {
        return offset;
    }

    int getSizeInBytes() {
        return sizeInBytes;
    }

    Range getRange() {
        return range;
    }
}

/**
 * Token bucket algorithm.
 */
class TokenBucket {
    private long size = Long.MAX_VALUE;
    TokenBucket() {
    }

    synchronized long take(long tokens) {
        if(tokens < this.size){
            this.size -= tokens;
            return tokens;
        }

        return 0;
    }

    synchronized void set(long tokens) {
        this.size = tokens;
    }
}

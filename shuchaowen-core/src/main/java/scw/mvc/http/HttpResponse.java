package scw.mvc.http;

import java.io.IOException;
import java.util.Collection;

import scw.mvc.Response;
import scw.net.http.Cookie;

public interface HttpResponse extends Response{
    void addCookie(Cookie cookie);
    
    void addCookie(String name, String value);

    boolean containsHeader(String name);

    void sendError(int sc, String msg) throws IOException;

    void sendError(int sc) throws IOException;

    void sendRedirect(String location) throws IOException;

    void setDateHeader(String name, long date);

    void addDateHeader(String name, long date);

    void setHeader(String name, String value);

    void addHeader(String name, String value);

    void setIntHeader(String name, int value);

    void addIntHeader(String name, int value);

    void setStatus(int sc);

    int getStatus();

    String getHeader(String name);

    Collection<String> getHeaders(String name);
    
    Collection<String> getHeaderNames();
    
    /**
     * Sets the length of the content body in the response In HTTP servlets,
     * this method sets the HTTP Content-Length header.
     *
     * @param length
     *            an integer specifying the length of the content being returned
     *            to the client; sets the Content-Length header
     *
     * @since Servlet 3.1
     */
    void setContentLength(long length);

    /**
     * Sets the preferred buffer size for the body of the response. The servlet
     * container will use a buffer at least as large as the size requested. The
     * actual buffer size used can be found using <code>getBufferSize</code>.
     * <p>
     * A larger buffer allows more content to be written before anything is
     * actually sent, thus providing the servlet with more time to set
     * appropriate status codes and headers. A smaller buffer decreases server
     * memory load and allows the client to start receiving data more quickly.
     * <p>
     * This method must be called before any response body content is written;
     * if content has been written or the response object has been committed,
     * this method throws an <code>IllegalStateException</code>.
     *
     * @param size
     *            the preferred buffer size
     * @exception IllegalStateException
     *                if this method is called after content has been written
     * @see #getBufferSize
     * @see #flushBuffer
     * @see #isCommitted
     * @see #reset
     */
    void setBufferSize(int size);

    /**
     * Returns the actual buffer size used for the response. If no buffering is
     * used, this method returns 0.
     *
     * @return the actual buffer size used
     * @see #setBufferSize
     * @see #flushBuffer
     * @see #isCommitted
     * @see #reset
     */
    int getBufferSize();

    /**
     * Forces any content in the buffer to be written to the client. A call to
     * this method automatically commits the response, meaning the status code
     * and headers will be written.
     *
     * @throws IOException if an I/O occurs during the flushing of the response
     *
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #isCommitted
     * @see #reset
     */
    void flushBuffer() throws IOException;

    void resetBuffer();

    /**
     * Returns a boolean indicating if the response has been committed. A
     * committed response has already had its status code and headers written.
     *
     * @return a boolean indicating if the response has been committed
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #flushBuffer
     * @see #reset
     */
    boolean isCommitted();

    /**
     * Clears any data that exists in the buffer as well as the status code and
     * headers. If the response has been committed, this method throws an
     * <code>IllegalStateException</code>.
     *
     * @exception IllegalStateException
     *                if the response has already been committed
     * @see #setBufferSize
     * @see #getBufferSize
     * @see #flushBuffer
     * @see #isCommitted
     */
    void reset();
}

<div>
  <h2>Manage Books</h2>
  {error && <p>Error: {error}</p>}
  <form onSubmit={(e) => { e.preventDefault(); handleAdd(); }}>
    <input name="isbn" value={formData.isbn} onChange={handleChange} placeholder="ISBN" />
    <input name="title" value={formData.title} onChange={handleChange} placeholder="Title" />
    <input name="author" value={formData.author} onChange={handleChange} placeholder="Author" />
    <button type="submit">Add Book</button>
  </form>
  <table>
    <thead><tr><th>ID</th><th>ISBN</th><th>Title</th><th>Author</th></tr></thead>
    <tbody>
      {books.map(book => (
        <tr key={book.id}>
          <td>{book.id}</td>
          <td>{book.isbn}</td>
          <td>{book.title}</td>
          <td>{book.author}</td>
        </tr>
      ))}
    </tbody>
  </table>
</div>

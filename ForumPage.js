import React, { useEffect, useState } from 'react';
import './ForumPage.css';

function ForumPage({ onMain, isLoggedIn, loggedUsername, userData }) {
  const [threads, setThreads] = useState([]);
  const [AllThreads, setAllThreads] = useState([]);
  const [selectedThreadId, setSelectedThreadId] = useState(null);
  const [newComment, setNewComment] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [isAddThreadPopupOpen, setAddThreadPopupOpen] = useState(false);
  const [newThreadTitle, setNewThreadTitle] = useState('');
  const [newThreadContent, setNewThreadContent] = useState('');
  const [isSearchActive, setSearchActive] = useState(false);
  const [AllComments, setAllComments] = useState([]);

  useEffect(() => {
    loadedForumData();
    loadedCommentData();
  }, []);

  const generateUniqueThreadId = () => {
    let newThreadId = threads.length + 1;

    while (threads.some((thread) => thread.thread_id === newThreadId)) {
      newThreadId++;
    }

    return newThreadId;
  };

  const generateUniqueCommentId = () => {
    
    const maxCommentId = AllComments.reduce((maxId, comment) => {
      return Math.max(maxId, comment.comment_id);
    }, 0);

    let newCommentId = maxCommentId +1;
    console.log("max comm id: "+newCommentId);
    return newCommentId;
  };

  async function loadedForumData() {
    try {
      const response = await fetch(`http://localhost:9999/forum/getThreads`);
      const data = await response.json();
  
      console.log("Data:");
      console.log(data);
  
      const threadsWithComments = data.threads.map((thread) => ({
        ...thread,
        comments: [],
      }));
  
      setAllThreads(threadsWithComments);
      setThreads(threadsWithComments.slice(-10).reverse());
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  }
  

  async function loadedCommentData() {
    try {
      const response = await fetch(`http://localhost:9999/forum/getComments`);
      const data = await response.json();
      
      console.log('Comment Data:');
      console.log(data);
      const threadsWithComments = threads.map((thread) => ({
        ...thread,
        comments: data.comments.filter((comment) => comment.thread_id === thread.thread_id),
      }));

      setThreads(threadsWithComments);
      setAllComments(data.comments);
    } catch (error) {
      console.error('Error fetching comment data:', error);
    }
    
  }

  const handleAddThread = async () => {
    const IdOfThread = generateUniqueThreadId();

    const newThread = {
      thread_id: IdOfThread,
      title: newThreadTitle,
      content: newThreadContent,
      comments: [],
    };

    setThreads((prevThreads) => [newThread, ...prevThreads.slice(0, 9)]);

    setAddThreadPopupOpen(false);

    setNewThreadTitle('');
    setNewThreadContent('');

    try {
      const response = await fetch(
        `http://localhost:9999/forum/addThread?thread_id=${IdOfThread}&title=${newThreadTitle}&content=${newThreadContent}&username=${loggedUsername}`,
        {
          method: 'PUT',
        }
      );

      if (response.ok) {
        console.log('Add thread successful');
      } else {
        console.error('Failed to add thread:', response.status, response.statusText);
      }
    } catch (error) {
      console.error('Error while adding thread:', error);
    }

    console.log(newThread);
  };

  const handleAddComment = async () => {
    if (selectedThreadId !== null) {
      const newCommentId = generateUniqueCommentId(selectedThreadId);

      const newCommentObject = {
        comment_id: newCommentId,
        username: loggedUsername,
        text: newComment,
        thread_id: selectedThreadId
      };


      try {
        const response = await fetch(
          `http://localhost:9999/forum/addComment?thread_id=${selectedThreadId}&comment_id=${newCommentId}&text=${newCommentObject.text}&username=${loggedUsername}`,
          {
            method: 'PUT',
          }
        );

        if (response.ok) {
          console.log('Add comment successful');
        } else {
          console.error('Failed to add comment:', response.status, response.statusText);
        }
      } catch (error) {
        console.error('Error while adding comment:', error);
      }

      setThreads((prevThreads) =>
        prevThreads.map((thread) =>
          thread.thread_id === selectedThreadId
            ? { ...thread, comments: [...thread.comments, newCommentObject] }
            : thread
        )
      );

    }
  };
  

  const handleThreadClick = async (threadId) => {
    setSelectedThreadId(threadId);
  
    const comments = await loadedCommentData(threadId);
  
  };

  const handleSearch = () => {
    if (searchQuery.trim() === '') {
      loadedForumData();
      setSearchQuery('');
      setSearchActive(false);
      return;
    }
    const filteredThreads = AllThreads.filter((thread) =>
      thread.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

    setThreads(filteredThreads.slice(0, 10));
    setSearchActive(true);

    setSearchQuery('');
  };

  return (
    <div className="forum-page">
      <div className="header">
        <div className="buttons">
          <button onClick={onMain}>Main</button>
        </div>
        <div className="user-info">
          <div className="logged-user">{loggedUsername}</div>
        </div>
      </div>
      <div className="content">
        <h2>Forum Page</h2>
        <div className="search-bar">
          <input
            type="text"
            placeholder="Search thread..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button onClick={handleSearch}>Search</button>
        </div>
        <div className="add-thread">
          <button onClick={() => setAddThreadPopupOpen(true)}>Add New Thread</button>
        </div>
        <div className="threads">
          <h3>{isSearchActive ? 'Searched Threads' : 'Last 10 Threads'}</h3>

          <ul>
          {threads.map((thread) => (
  <li key={thread.thread_id} onClick={() => handleThreadClick(thread.thread_id)}>
    <div>
      <strong>Title:</strong> {thread.title}
    </div>
    <div>
      <strong>Content:</strong> {thread.content}
    </div>
    {selectedThreadId === thread.thread_id && (
      <div className="comments-section">
        <input
          type="text"
          placeholder="Add a comment..."
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
        />
        <button onClick={handleAddComment}>Add Comment</button>
        <ul>
          {thread.comments.map((comment, index) => (
            <li key={index}>
              <strong>{comment.username}:</strong> {comment.text}
            </li>
          ))}
        </ul>
      </div>
    )}
  </li>
))}
</ul>

        </div>
        {isAddThreadPopupOpen && (
          <div className="add-thread-popup">
            <label>Title:</label>
            <input
              type="text"
              placeholder="Enter thread title..."
              value={newThreadTitle}
              onChange={(e) => setNewThreadTitle(e.target.value)}
            />
            <label>Content:</label>
            <textarea
              placeholder="Enter thread content..."
              value={newThreadContent}
              onChange={(e) => setNewThreadContent(e.target.value)}
            />
            <div className="button-row">
              <button onClick={handleAddThread}>Add Thread</button>
              <button onClick={() => setAddThreadPopupOpen(false)}>Cancel</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default ForumPage;

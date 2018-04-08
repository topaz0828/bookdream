import React from 'react';

class AddContentsModal extends React.Component {
	constructor(props) {
		super(props)
		this.app = props.app;
	}

	componentDidMount() {
		this.contentsView = this.app.contents;
		this.myPageView = this.app.myPage;
		
		this.searchBookInput = $('#searchBookInput');
		this.searchBookInput.on('keypress', (events) => {
			if (events.keyCode == 13) {
				this.findBook();
			}
		});
		$('#searchBookButton').on('click', () => {
			this.findBook();
		});
		this.impressionBack = $('#impressionBack');
		this.reviewBack = $('#reviewBack');

		this.impressionButton = $('#impressionButton');
		this.impressionButton.on('click', () => {
			this.impressionButton.addClass('active');
			this.impressionBack.show();
			this.reviewButton.removeClass('active');
			this.reviewBack.hide();
		});
		this.reviewButton = $('#reviewButton');
		this.reviewButton.on('click', () => {
			this.impressionButton.removeClass('active');
			this.impressionBack.hide();
			this.reviewButton.addClass('active');
			this.reviewBack.show();
		});
		this.searchResultDropdown = $('#searchResultDropdown');
		this.selectedBookView = $('#selectedBookView');
		this.selectedBook = null;
	}

	findBook() {
		var self = this;
		var inputValue = this.searchBookInput.val();
		if (inputValue.length > 0) {
			$.ajax({
				type: 'GET',
				url: '/api/search/book',
				dataType: 'json',
				data: {q: inputValue},
			}).done(function(res) {
				self.searchResultDropdown.empty();

				var ul = document.createElement('ul');
				ul.className = 'dropdown-menu';
				ul.setAttribute('role', 'menu');
				ul.style.width = '100%';
				for (var i in res) {
					var a = document.createElement('a');
					a.setAttribute('role', 'menuitem');
					a.setAttribute('tabIndex', '-1');
					a.style.fontSize = '17px';
					a.style.cursor = 'pointer';
					a.textContent = res[i].title;
					a.searchInfo = res[i];
					$(a).click(function(event) {
						self.selectBook(event.target.searchInfo);
					});
					var li = document.createElement('li');
					li.setAttribute('role', 'presentation');
					$(li).append(a);
					$(ul).append(li);
				}

				self.searchResultDropdown.append(ul);
				self.searchResultDropdown.addClass('open');
			}).fail(function() {
				alert('Server error.');
			});
		}
	}

	selectBook(book) {
		// console.log(book);
		this.selectedBook = book;
		this.selectedBookView.html('<table>' + 
										'<tr><td style="padding-left: 20px;"><img src="' + book.thumbnail + '" style="border:1px solid black;"/></td>' + 
										'<td style="padding-left:20px;"><h4>' + book.title + '</h4>' + book.author + ' (' + book.publisher + ')</td></tr>' +
									'</table>');
	
		this.searchBookInput.val('');
		this.searchResultDropdown.removeClass('open');
	}

	save() {
		if (this.selectedBook != null) {
			var url, data = {};
			data.book = this.selectedBook;
			this.selectedBook = null;

			if (this.impressionButton.hasClass('active')) {
				data.impression = [];
				url = '/api/user/impression';
				var impressionInput = $('input[name=impressionInput]');
				for (var i in impressionInput) {
					var text = impressionInput[i].value;
					if (text && text.length > 0) {
						data.impression.push(text);
					}
				}

				if (data.impression.length == 0) {
					return;
				}
			} else {
				url = '/api/user/review';
				data.review = $('#reviewInput').val();
				if (data.review.length ==  0) {
					return;
				}
			}
			
			$('input[name=impressionInput]').val('');
			$('#reviewInput').val('');
			var self = this;
			$.ajax({
				type: 'POST',
				url: url,
				data: JSON.stringify(data),
			}).done(function() {
				self.close();
				self.contentsView.refresh();
				self.myPageView.refresh();
			}).fail(function() {
				alert('Server error.');
				self.close();
			});
		}
	}

	close() {
		this.selectedBook = null;
		this.selectedBookView.empty()
		this.searchBookInput.val('');
		this.searchResultDropdown.removeClass('open');
	}

	renderInputView() {
		return (
			<div>
				<div className="input-group">
					<input id='searchBookInput' type="text" className="form-control" placeholder="Book Finder" aria-describedby="sizing-addon2"/>
					<span className="input-group-btn">
						<button id='searchBookButton' className="btn btn-default" type="button">
							<span className='glyphicon glyphicon-search' aria-hidden='true'></span>
						</button>
					</span>
				</div>
				<div id='searchResultDropdown' className="dropdown" width='100%'>
					<ul className="dropdown-menu" role="menu" style={{width:'100%'}}>
					</ul>
				</div>
			</div>
		);
	}

	renderImpressionAndReviewView() {
		return (
			<div>
				<div id='selectedBookView' style={{paddingTop: '10px'}}>
				</div>
				<div style={{paddingTop: '10px', paddingBottom: '10px'}}>
					<ul className="nav nav-tabs">
						<li role="presentation" className="active" id='impressionButton'><a href="#">Impression</a></li>
						<li role="presentation" id='reviewButton'><a href="#">Review</a></li>
					</ul>
				</div>
				<div id='impressionBack' className='form-group'>
					<div style={{paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 1st impression' aria-describedby="sizing-addon2" maxLength='200'/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 2nd impression' aria-describedby="sizing-addon2" maxLength='200'/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 3rd impression' aria-describedby="sizing-addon2" maxLength='200'/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 4st impression' aria-describedby="sizing-addon2" maxLength='200'/>
					</div>
					<div style={{paddingTop:'10px', paddingBottom:'10px'}}>
						<input name='impressionInput' type="text" className="form-control" placeholder='Enter 5st impression' aria-describedby="sizing-addon2" maxLength='200'/>
					</div>
				</div>
				<div id='reviewBack' hidden='true' className='form-group'>
					<textarea id='reviewInput' className='form-control' style={{height:'260px', resize:'none'}}></textarea>
				</div>
			</div>
		);
	}

	render() {
		return (
			<div className="modal fade" id="addContentsModal" tabIndex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
				<div className="modal-dialog">
					<div className="modal-content">
						<div className="modal-header">
							<button type="button" className="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							<h4 className="modal-title" id="myModalLabel">Add impression or review</h4>
						</div>
						<div className="modal-body">
							{this.renderInputView()}
							{this.renderImpressionAndReviewView()}
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal" onClick={() => this.close()}>Close</button>
							<button type="button" className="btn btn-primary" data-dismiss="modal" onClick={() => this.save()}>Save</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default AddContentsModal;
import React from 'react';

class AddContentsModal extends React.Component {
	constructor(props) {
		super(props);
		this.app = props.app;
		this.state = {impressionTextCounter: '0/200', reviewTextCounter: '0/∞'};
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
		$('#addContentsModal').on('shown.bs.modal', function () {
			$('#searchBookInput').focus()
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

	showTextCount() {
		this.setState({impressionTextCounter: this.refs.impressionInput.value.length + '/200',
						reviewTextCounter: this.refs.reviewInput.value.length + '/∞'});
	}

	show() {
		
		this.reset();
		this.showTextCount();
		$('#addContentsModal').modal({backdrop: 'static'});
		this.searchBookInput.focus();
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
				ul.style.maxHeight = '500px';
				ul.style.overflowY = 'auto';
				for (var i in res) {
					var li = document.createElement('li');
					li.setAttribute('role', 'presentation');

					self.makeSearchResultRow(li, res[i]);
					$(ul).append(li);
				}

				self.searchResultDropdown.append(ul);
				self.searchResultDropdown.addClass('open');
			}).fail(function() {
				alert('Server error.');
			});
		}
	}

	makeSearchResultRow(parent, book) {
		var self = this;
		var a = document.createElement('a');
		a.setAttribute('role', 'menuitem');
		a.setAttribute('tabIndex', '-1');
		a.style.fontSize = '15px';
		a.style.cursor = 'pointer';
		
		var div = document.createElement('div');
		div.innerHTML = '<img src="' + book.thumbnail + '" style="border:1px solid black; max-width: 80px;"/>';
		div.innerHTML += '<span style="max-width: 450px; display: inline-block; white-space: normal; padding-left: 5px;">' + book.title + '</span>'
		div.searchInfo = book;
		$(div).click(function(event) {
			if (event.target.localName === 'div') {
				self.selectBook(event.target.searchInfo);
			} else {
				self.selectBook($(event.target).parent()[0].searchInfo);
			}
		});
		a.appendChild(div);

		parent.appendChild(a);
	}

	selectBook(book) {
		this.selectedBook = book;
		this.selectedBookView.html('<table>' + 
										'<tr><td style="padding-left: 10px;"><img src="' + book.thumbnail + '" style="border:1px solid black;"/></td>' + 
										'<td style="padding-left:10px;"><h5>' + book.title + '</h5>' + book.author + ' (' + book.publisher + ')</td></tr>' +
									'</table>');
		this.searchResultDropdown.removeClass('open');
	}

	save(isClose) {
		if (this.selectedBook) {
			this.saveImpression();
			this.saveReview();
			this.props.app.refresh();

			if (isClose) {
				$('#addContentsModal').modal('hide');
			}
		}
	}

	saveImpression() {
		var data = {};
		data.book = this.selectedBook;
		data.impression = [];
		
		var impressionInput = $('textarea[name=impressionInput]');
		for (var i in impressionInput) {
			var text = impressionInput[i].value;
			if (text && text.length > 0) {
				data.impression.push(text);
			}
		}

		if (data.impression.length == 0) {
			return;
		}

		var self = this;
		$.ajax({
			type: 'POST',
			url: '/api/contents/impression',
			data: JSON.stringify(data),
			async: false
		}).done(function() {
			$('textarea[name=impressionInput]').val('');
		}).fail(function() {
			alert('Server error.');
		});
	}

	saveReview() {
		var data = {};
		data.book = this.selectedBook;
		data.review = $('#reviewInput').val();
		if (data.review.length ==  0) {
			return;
		}

		var self = this;
		$.ajax({
			type: 'POST',
			url: '/api/contents/review',
			data: JSON.stringify(data),
			async: false
		}).done(function() {
			$('#reviewInput').val('');
		}).fail(function() {
			alert('Server error.');
		});
	}

	reset() {
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
						<li role="presentation" className="active" id='impressionButton'><a>감명깊게 읽은 문구</a></li>
						<li role="presentation" id='reviewButton'><a>후기</a></li>
					</ul>
				</div>
				<div id='impressionBack' className='form-group' style={{textAlign: 'right'}}>
					<div className='form-group'>
						<textarea ref='impressionInput' name='impressionInput' className='form-control' placeholder='감명깊게 읽은 문구를 적어주세요' style={{height:'150px', resize:'none'}} maxLength='200' 
							onChange={() => this.showTextCount()}></textarea>
						{this.state.impressionTextCounter}&nbsp;&nbsp;
					</div>
				</div>
				<div id='reviewBack' hidden='true' className='form-group' style={{textAlign: 'right'}}>
					<textarea ref='reviewInput' id='reviewInput' className='form-control' placeholder='후기를 적어주세요' style={{height:'300px', resize:'none'}} onChange={() => this.showTextCount()}></textarea>
					{this.state.reviewTextCounter}&nbsp;&nbsp;
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
							<h4 className="modal-title" id="myModalLabel">감명깊은 문구 / 후기</h4>
						</div>
						<div className="modal-body">
							{this.renderInputView()}
							{this.renderImpressionAndReviewView()}
						</div>
						<div className="modal-footer">
							<button type="button" className="btn btn-default" data-dismiss="modal">닫기</button>
							<button type="button" className="btn btn-primary" onClick={() => this.save(true)}>저장 후 닫기</button>
							<button type="button" className="btn btn-primary" onClick={() => this.save(false)}>저장</button>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default AddContentsModal;
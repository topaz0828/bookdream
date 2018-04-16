import React from 'react';

class Card extends React.Component {
	constructor(props) {
		super(props);
		this.parent = props.parent;
		this.showDetailModal = () => {
			this.parent.app.showDetailModal(this.props);
		}
		// this.colorClass = 'thumbnail alert-success';
		// if (this.props.type === 'R') {
		// 	this.colorClass = 'thumbnail alert-warning';
		// }
		this.onMouseOver = () => {
			// this.thumbnameDiv.className = 'thumbnail';
			this.thumbnameDiv.style.cursor = 'pointer';
		}
		this.onMouseOut = () => {
			// this.thumbnameDiv.className = this.colorClass;
			this.thumbnameDiv.style.cursor = 'arrow';
		}
		//<span name='updateDate'>{this.props.updateDate}</span>
	}

	render() {
		// var className = 'thumbnail ' + this.colorClass;
		var className = 'thumbnail bookdream-card';
		return (
			<div ref={ref => this.thumbnameDiv = ref} className={className} onClick={this.showDetailModal} onMouseOver={this.onMouseOver} onMouseOut={this.onMouseOut}>
				<div className='caption'>
					<div style={{paddingBottom:'10px'}}>
						<table width='100%'>
							<tbody>
							<tr>
								<td valign='top'>
									<h5 name='title' style={{paddingRight:'20px'}}><strong>{this.props.title}</strong></h5>
									<p><span name='author'>{this.props.author}</span></p>
								</td>
								<td style={{paddingRight:'10px'}}>
									<img name='image' src={this.props.image} width='100px' style={{float:'right'}}/>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
					<div align='left'><p><span name='text'>{this.props.text}</span></p></div>
					<div align='right'><p><span name='nickname'>{this.props.nickname}</span></p></div>
					<input type='hidden' name='contentsId' value={this.props.contentsId}/>
					<input type='hidden' name='bookId' value={this.props.bookId}/>
					<input type='hidden' name='type' value={this.props.type}/>
					<input type='hidden' name='my' value={this.props.my}/>
				</div>
			</div>
		);
	}
}

export default Card;
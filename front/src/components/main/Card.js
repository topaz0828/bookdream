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
									<h5 style={{paddingRight:'20px'}}><strong>{this.props.title}</strong></h5>
									<p name='author'>{this.props.author}</p>
								</td>
								<td style={{paddingRight:'10px'}}>
									<img name='image' src={this.props.image} width='100px' style={{float:'right'}}/>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
					<div align='left'><p name='text'>{this.props.text}</p></div>
					<div align='right'><p><span name='nickname'>{this.props.nickname}</span></p></div>
					<input type='hidden' name='type' value={this.props.type}/>
				</div>
			</div>
		);
	}
}

export default Card;
import React, { Component } from 'react';

class DynamicProperty extends Component {


  constructor(props) {
    super(props);
    
    this.handleChange = this.handleChange.bind(this);    
  }
  
  handleChange(event) {
    this.props.onChange(this.props.path, this._getValueFromEvent(event));
  }

  _getValueFromEvent(event) {
    if(!event)
      return '';
    if(!event.target)
      return event.value;
    const element = event.target;
    return (element.type === 'checkbox') ? element.checked : element.value;
  }

  render() {
    const meta  = this.props.meta;
    const value = this.props.value;
    const id    = this.props.name + "Field";
    const handleChange = this.handleChange;
    const _this = this;

    const controls = {
      checkBox: {
        normal: function() {
          return ( React.DOM.input({id: id, key: id, type: "checkbox", checked: value, onChange: handleChange}));
        },
        readOnly: function() {
          return ( React.DOM.input({id: id, key: id, type: "checkbox", disabled: true, checked: value}) );
        }
      },
      comboBox: {
        normal: () => {
          var options = meta.options.map(function(option) {
            return ( React.DOM.option({key: option.value, value: option.value}, option.text) );
          });
          if(meta.canBeNull){
            options.unshift(  ( React.DOM.option({key: "", value: ""}, "") ) );
          }
          return (
            React.DOM.select({id: id, ref: 'editableComboBox', key: id, defaultValue: value, onChange: handleChange, className: this.props.controlClassName},
              options
            )
          );
        },
        readOnly: () => {
          const selectedOption = meta.options.filter(option => option.value === meta.value);
          const text = selectedOption.length ? selectedOption[0].text : meta.value;
          return this.createStatic(text);
        }
      },
      textArea: {
        normal: () => (
          <textarea placeholder={meta.placeholder} id={id}  rows={meta.rows} cols={meta.columns} value={value} onChange={handleChange} className={this.props.controlClassName}/>
        ),
        readOnly: () => this.createStatic(value)
      },
      textInput: {
        normal: <input type="text" className="form-control" placeholder={meta.placeholder} id={id} key={id} value={value}
                       onChange={handleChange} />,
        readOnly: this.createStatic(value)
      },
      passwordInput: {
        normal: <input type="password" placeholder={meta.placeholder} id={id} key={id} value={value}
                       onChange={handleChange} className={this.props.controlClassName}/>,
        readOnly: this.createStatic('******')
      }
    };

    const renderer = controls[meta.type] || controls['textInput'];
    const valueControl = renderer[meta.readOnly ? 'readOnly' : 'normal'];
    const label = <label htmlFor={id} className={this.props.labelClassName}>{meta.displayName}</label>;
    const helpTextElement = meta.helpText ? <span className={this.props.helpTextClassName!=null?this.props.helpTextClassName:"help-block"}>{meta.helpText}</span> : undefined;
    const hasDanger = !meta.error && value === '' ? 'error' : '';

    return (
      <div className={(this.props.className!=null?this.props.className:'form-group dynamic-property') + ' ' + hasDanger}>
        {label}
        <div className="controls">
          {valueControl}
          {helpTextElement}
        </div>
      </div>
    );
  }

  createStatic(value) {
    return <p className="form-control-static" dangerouslySetInnerHTML={{__html: value}}></p>;
  }

}

export default DynamicProperty;
